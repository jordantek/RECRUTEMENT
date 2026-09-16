# document_digitalizer_fixed.py
import cv2
import numpy as np
import qrcode
import json
import sys
import os
import base64
from datetime import datetime
from PIL import Image
import fitz  # PyMuPDF
from io import BytesIO

class ConservativeDocumentDigitalizer:
    def __init__(self):
        self.output_dir = "storage/processed"
        os.makedirs(self.output_dir, exist_ok=True)

    def process_document(self, file_path, employe_id, document_type):
        try:
            print(f"[INFO] Début du traitement: {file_path}")

            if file_path.lower().endswith('.pdf'):
                return self.process_pdf_conservative(file_path, employe_id, document_type)
            else:
                return self.process_image_conservative(file_path, employe_id, document_type)

        except Exception as e:
            print(f"[ERROR] Erreur traitement: {str(e)}")
            return self.error_response(f"Erreur: {str(e)}")

    def process_pdf_conservative(self, file_path, employe_id, document_type):
        """Traiter un PDF en préservant le texte natif"""
        try:
            pdf_document = fitz.open(file_path)
            total_signatures = 0
            total_stamps = 0

            for page_num in range(len(pdf_document)):
                page = pdf_document[page_num]
                print(f"[INFO] Traitement page {page_num + 1}...")

                # SOLUTION: Détecter et supprimer les images/dessins sans toucher au texte
                sig_count, stamp_count = self.remove_signatures_preserve_text(page)
                total_signatures += sig_count
                total_stamps += stamp_count

                if sig_count > 0 or stamp_count > 0:
                    print(f"[SUCCESS] Page {page_num + 1}: {sig_count} signatures, {stamp_count} cachets supprimés")

            # CORRECTION: Ajouter le QR Code en HAUT À DROITE de la PREMIÈRE page
            first_page = pdf_document[0]
            self.add_qr_code_top_right(first_page, employe_id, document_type)

            # Sauvegarder le PDF
            base_name = os.path.splitext(os.path.basename(file_path))[0]
            output_path = os.path.join(self.output_dir, f"RECONSTRUCTED_{base_name}.pdf")
            pdf_document.save(output_path, garbage=4, deflate=True)
            pdf_document.close()

            # Générer les données
            qr_data = self.generate_qr_data(
                employe_id, document_type, file_path,
                total_signatures, total_stamps, len(pdf_document)
            )
            qr_base64 = self.generate_qr_code(qr_data)

            return {
                "status": "success",
                "processed_file_path": output_path,
                "file_type": "pdf",
                "qr_code_base64": qr_base64,
                "signatures_removed": total_signatures,
                "stamps_removed": total_stamps,
                "pages_processed": len(pdf_document),
                "digital_data": qr_data,
                "note": f"Texte préservé - {total_signatures} signatures et {total_stamps} cachets supprimés"
            }

        except Exception as e:
            print(f"[ERROR] Erreur traitement PDF: {str(e)}")
            import traceback
            traceback.print_exc()
            return self.error_response(f"Erreur PDF: {str(e)}")

    def add_qr_code_top_right(self, page, employe_id, document_type):
        """Ajouter un QR Code BIEN VISIBLE en HAUT À DROITE de la page"""
        try:
            # Générer le QR Code avec une qualité supérieure
            qr_data = self.generate_qr_data(employe_id, document_type, "", 0, 0, 1)
            qr = qrcode.QRCode(
                version=1,
                box_size=12,  # Taille optimale pour bonne lisibilité
                border=3
            )
            qr.add_data(json.dumps(qr_data, ensure_ascii=False))
            qr.make(fit=True)
            qr_img = qr.make_image(fill_color="black", back_color="white")

            # Convertir en bytes avec bonne résolution
            img_bytes = BytesIO()
            qr_img.save(img_bytes, format='PNG', dpi=(300, 300))  # Haute résolution
            img_bytes = img_bytes.getvalue()

            # CORRECTION: Position en HAUT À DROITE avec taille adaptée
            rect = page.rect
            qr_size = 80  # Taille réduite pour mieux s'intégrer en haut de page
            margin_x = 30  # Marge depuis le bord droit
            margin_y = 30  # Marge depuis le bord supérieur

            # Rectangle de positionnement en HAUT À DROITE
            qr_rect = fitz.Rect(
                rect.width - qr_size - margin_x,  # À droite
                margin_y,                         # En haut
                rect.width - margin_x,            # Bord droit
                margin_y + qr_size                # Bas du QR Code
            )

            # Nettoyer la zone avant d'insérer le QR Code
            page.add_redact_annot(qr_rect, fill=(1, 1, 1))  # Fond blanc
            page.apply_redactions()

            # Ajouter le QR Code
            page.insert_image(qr_rect, stream=img_bytes, keep_proportion=True)

            # CORRECTION: Ajouter un texte descriptif AU-DESSUS du QR Code
            text_rect = fitz.Rect(
                rect.width - qr_size - margin_x - 10,  # Un peu à gauche du QR
                margin_y - 20,                         # Au-dessus du QR
                rect.width - margin_x,
                margin_y - 5
            )

            # Texte plus discret mais informatif
            page.insert_textbox(
                text_rect,
                "DOC. NUMÉRISÉ",
                fontsize=8,
                color=(0.3, 0.3, 0.3),  # Gris discret
                align=2  # Alignement à droite
            )

            print(f"[SUCCESS] QR Code ajouté en haut à droite - Position: {qr_rect}")

        except Exception as e:
            print(f"[WARN] Erreur ajout QR Code: {e}")
            import traceback
            traceback.print_exc()

    def remove_signatures_preserve_text(self, page):
        """Supprimer les signatures en préservant le texte natif du PDF"""
        sig_count = 0
        stamp_count = 0

        try:
            # Obtenir toutes les images de la page
            image_list = page.get_images(full=True)

            # Parcourir chaque image
            for img_index, img in enumerate(image_list):
                xref = img[0]

                # Obtenir les informations de l'image
                try:
                    bbox_list = page.get_image_rects(xref)

                    for bbox in bbox_list:
                        # Extraire l'image pour analyse
                        base_image = page.parent.extract_image(xref)
                        image_bytes = base_image["image"]

                        # Convertir en numpy array
                        nparr = np.frombuffer(image_bytes, np.uint8)
                        img_cv = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

                        if img_cv is None:
                            continue

                        # Analyser si c'est une signature ou un cachet
                        is_signature, is_stamp = self.analyze_image_content(img_cv, bbox)

                        if is_signature or is_stamp:
                            # Supprimer l'image en la recouvrant de blanc
                            page.add_redact_annot(bbox, fill=(1, 1, 1))

                            if is_signature:
                                sig_count += 1
                                print(f"[DETECTION] Signature image détectée et supprimée")
                            else:
                                stamp_count += 1
                                print(f"[DETECTION] Cachet image détecté et supprimé")

                except Exception as e:
                    print(f"[WARN] Erreur analyse image {img_index}: {e}")
                    continue

            # Appliquer toutes les suppressions
            page.apply_redactions()

            # Détecter les dessins vectoriels (signatures manuscrites)
            drawings = page.get_drawings()
            for drawing in drawings:
                rect = drawing.get("rect")
                if rect:
                    # Analyser la zone du dessin
                    if self.is_likely_signature_drawing(drawing, rect, page):
                        # Supprimer le dessin
                        page.add_redact_annot(rect, fill=(1, 1, 1))
                        sig_count += 1
                        print(f"[DETECTION] Signature vectorielle détectée")

            # Appliquer les suppressions de dessins
            page.apply_redactions()

        except Exception as e:
            print(f"[ERROR] Erreur suppression signatures: {e}")

        return sig_count, stamp_count

    def analyze_image_content(self, img_cv, bbox):
        """Analyser si une image est une signature ou un cachet"""
        try:
            height, width = img_cv.shape[:2]

            # Convertir en niveaux de gris
            gray = cv2.cvtColor(img_cv, cv2.COLOR_BGR2GRAY)

            # Calculer le pourcentage de pixels non blancs
            _, binary = cv2.threshold(gray, 240, 255, cv2.THRESH_BINARY_INV)
            non_white_ratio = np.count_nonzero(binary) / (height * width)

            # Dimensions du bbox
            bbox_width = bbox.width
            bbox_height = bbox.height

            # Critères pour signature manuscrite
            is_signature = (
                    50 < bbox_width < 400 and
                    20 < bbox_height < 150 and
                    bbox_width > bbox_height * 1.3 and
                    0.05 < non_white_ratio < 0.4
            )

            # Critères pour cachet
            is_stamp = (
                    40 < bbox_width < 250 and
                    40 < bbox_height < 250 and
                    0.7 < (bbox_width / bbox_height) < 1.3 and
                    0.1 < non_white_ratio < 0.6
            )

            return is_signature, is_stamp

        except Exception as e:
            print(f"[WARN] Erreur analyse contenu: {e}")
            return False, False

    def is_likely_signature_drawing(self, drawing, rect, page):
        """Vérifier si un dessin vectoriel est probablement une signature"""
        try:
            # Vérifier la position (souvent en bas de page)
            page_height = page.rect.height
            is_bottom_third = rect.y0 > page_height * 0.6

            # Vérifier les dimensions
            width = rect.width
            height = rect.height
            is_signature_size = (
                    50 < width < 400 and
                    20 < height < 150 and
                    width > height * 1.2
            )

            return is_bottom_third and is_signature_size

        except Exception as e:
            print(f"[WARN] Erreur analyse dessin: {e}")
            return False

    def process_image_conservative(self, file_path, employe_id, document_type):
        """Traiter une image avec QR Code en haut à droite"""
        try:
            image = cv2.imread(file_path)
            if image is None:
                return self.error_response("Image non lisible")

            # Détecter et supprimer les signatures
            cleaned_image, sig_count, stamp_count = self.remove_signatures_from_image(image)

            # CORRECTION: Ajouter le QR Code en HAUT À DROITE
            final_image = self.add_qr_top_right_to_image(cleaned_image, employe_id, document_type)

            # Sauvegarder
            base_name = os.path.splitext(os.path.basename(file_path))[0]
            output_path = os.path.join(self.output_dir, f"RECONSTRUCTED_{base_name}.png")
            cv2.imwrite(output_path, final_image)

            # Générer QR Code pour réponse
            qr_data = self.generate_qr_data(employe_id, document_type, file_path, sig_count, stamp_count, 1)
            qr_base64 = self.generate_qr_code(qr_data)

            return {
                "status": "success",
                "processed_file_path": output_path,
                "file_type": "image",
                "qr_code_base64": qr_base64,
                "signatures_removed": sig_count,
                "stamps_removed": stamp_count,
                "pages_processed": 1,
                "digital_data": qr_data
            }

        except Exception as e:
            print(f"[ERROR] Erreur traitement image: {str(e)}")
            return self.error_response(f"Erreur image: {str(e)}")

    def add_qr_top_right_to_image(self, image, employe_id, document_type):
        """Ajouter QR Code en HAUT À DROITE d'une image"""
        try:
            qr_data = self.generate_qr_data(employe_id, document_type, "", 0, 0, 1)
            qr = qrcode.QRCode(
                version=1,
                box_size=10,  # Taille optimale
                border=3
            )
            qr.add_data(json.dumps(qr_data, ensure_ascii=False))
            qr.make(fit=True)
            qr_img = qr.make_image(fill_color="black", back_color="white")

            # Convertir en numpy
            qr_np = np.array(qr_img.convert('RGB'))
            qr_cv = cv2.cvtColor(qr_np, cv2.COLOR_RGB2BGR)

            # CORRECTION: Taille adaptée pour le haut de page
            qr_size = 120
            qr_resized = cv2.resize(qr_cv, (qr_size, qr_size))

            # Positionner en HAUT À DROITE
            h, w = image.shape[:2]
            margin_x = 30
            margin_y = 30

            # Créer un fond blanc pour le QR Code
            white_bg = np.ones((qr_size + 10, qr_size + 10, 3), dtype=np.uint8) * 255

            # Placer le fond blanc
            bg_y_start = margin_y - 5
            bg_y_end = margin_y + qr_size + 5
            bg_x_start = w - qr_size - margin_x - 5
            bg_x_end = w - margin_x + 5

            # Vérifier les limites de l'image
            bg_y_start = max(0, bg_y_start)
            bg_y_end = min(h, bg_y_end)
            bg_x_start = max(0, bg_x_start)
            bg_x_end = min(w, bg_x_end)

            bg_height = bg_y_end - bg_y_start
            bg_width = bg_x_end - bg_x_start

            if bg_height > 0 and bg_width > 0:
                white_bg_resized = cv2.resize(white_bg, (bg_width, bg_height))
                image[bg_y_start:bg_y_end, bg_x_start:bg_x_end] = white_bg_resized

            # Placer le QR Code
            qr_y_start = margin_y
            qr_y_end = margin_y + qr_size
            qr_x_start = w - qr_size - margin_x
            qr_x_end = w - margin_x

            # Vérifier les limites
            qr_y_start = max(0, qr_y_start)
            qr_y_end = min(h, qr_y_end)
            qr_x_start = max(0, qr_x_start)
            qr_x_end = min(w, qr_x_end)

            qr_height = qr_y_end - qr_y_start
            qr_width = qr_x_end - qr_x_start

            if qr_height > 0 and qr_width > 0:
                qr_resized_final = cv2.resize(qr_resized, (qr_width, qr_height))
                image[qr_y_start:qr_y_end, qr_x_start:qr_x_end] = qr_resized_final

            # Ajouter une bordure noire fine autour du QR
            cv2.rectangle(
                image,
                (qr_x_start, qr_y_start),
                (qr_x_end, qr_y_end),
                (0, 0, 0),
                1
            )

            # CORRECTION: Texte au-dessus du QR Code
            text_x = qr_x_start
            text_y = max(20, margin_y - 10)  # Au-dessus du QR Code

            cv2.putText(
                image,
                "DOC NUMERISE",
                (text_x, text_y),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.5,  # Taille de police réduite
                (0.3, 0.3, 0.3),  # Gris discret
                1,
                cv2.LINE_AA
            )

            return image
        except Exception as e:
            print(f"[WARN] Erreur ajout QR: {e}")
            return image

    def remove_signatures_from_image(self, image):
        """Supprimer les signatures d'une image"""
        cleaned = image.copy()
        sig_count = 0
        stamp_count = 0

        try:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            _, binary = cv2.threshold(gray, 200, 255, cv2.THRESH_BINARY_INV)

            # Détecter les contours
            contours, _ = cv2.findContours(binary, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

            for contour in contours:
                x, y, w, h = cv2.boundingRect(contour)
                area = cv2.contourArea(contour)

                # Signature
                if 500 < area < 15000 and 50 < w < 400 and 20 < h < 150 and w > h * 1.3:
                    cv2.rectangle(cleaned, (x-5, y-5), (x+w+5, y+h+5), (255, 255, 255), -1)
                    sig_count += 1

                # Cachet
                elif 1000 < area < 20000 and 0.7 < w/h < 1.3 and w > 40:
                    cv2.rectangle(cleaned, (x-5, y-5), (x+w+5, y+h+5), (255, 255, 255), -1)
                    stamp_count += 1

        except Exception as e:
            print(f"[WARN] Erreur suppression: {e}")

        return cleaned, sig_count, stamp_count

    def generate_qr_data(self, employe_id, doc_type, file_path, sig_count, stamp_count, page_count):
        return {
            "document_id": f"DIGI_{employe_id}_{int(datetime.now().timestamp())}",
            "employe_id": employe_id,
            "document_type": doc_type,
            "original_file": os.path.basename(file_path) if file_path else "unknown",
            "digitalization_date": datetime.now().isoformat(),
            "signatures_removed": sig_count,
            "stamps_removed": stamp_count,
            "pages_processed": page_count,
            "version": "4.1",
            "note": "QR Code positionné en haut à droite"
        }

    def generate_qr_code(self, data):
        try:
            qr = qrcode.QRCode(version=1, box_size=10, border=4)
            qr.add_data(json.dumps(data, ensure_ascii=False))
            qr.make(fit=True)
            qr_img = qr.make_image(fill_color="black", back_color="white")
            buffered = BytesIO()
            qr_img.save(buffered, format="PNG")
            return base64.b64encode(buffered.getvalue()).decode()
        except Exception as e:
            print(f"[ERROR] Erreur generation QR Code: {e}")
            return ""

    def error_response(self, message):
        return {
            "status": "error",
            "error_message": message
        }

def main():
    if len(sys.argv) < 4:
        error_result = {
            "status": "error",
            "error_message": "Arguments manquants: fichier employe_id document_type"
        }
        print(json.dumps(error_result))
        sys.exit(1)

    file_path = sys.argv[1]
    employe_id = sys.argv[2]
    document_type = sys.argv[3]

    if not os.path.exists(file_path):
        error_result = {
            "status": "error",
            "error_message": f"Fichier introuvable: {file_path}"
        }
        print(json.dumps(error_result))
        sys.exit(1)

    processor = ConservativeDocumentDigitalizer()
    result = processor.process_document(file_path, employe_id, document_type)

    print(json.dumps(result, ensure_ascii=False))

if __name__ == "__main__":
    main()

