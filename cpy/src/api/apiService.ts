import ToastHelpers from '@/helpers/ToastHelpers';
import axios from 'axios';
import { genralConfig } from '../../config';
import { toast } from 'sonner';
import { useModalStore } from '@/contexts/modalUserLogginStore';
import { ApiResponseTypes } from '@/types/ApiResponseTypes';
import { UseFormReturn } from 'react-hook-form';
import {renderErrorList} from "@/api/render-error-list.tsx";

const apiClient = axios.create({
    baseURL: genralConfig.apiUrl,
    headers: { 'Content-Type': 'application/json' },
});

const get = async (
    data: { url: string; params?: unknown; headers?: unknown | null },
    miscellaneous: {
        userToken: string;
        hasNoSuccessModal?: boolean;
        onTokenExpired?: () => void;
    } | null = null
) => {
    try {
        const response = await apiClient.get(data.url, {
            params: data.params,
            headers: {
                Authorization: `${miscellaneous?.userToken}`,
                ...(data?.headers ?? {}),
            },
        });

        if (!miscellaneous?.hasNoSuccessModal) {
            handleSucess(response.data);
        }

        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;

            if (status === 401 && error.response?.data?.expiredToken) {
                miscellaneous?.onTokenExpired?.();
            }

            throw new Error(JSON.stringify(error.response?.data));
        }

        throw error;
    }
};

const post = async (
    data: { url: string; body?: unknown | null; headers?: unknown | null },
    miscellaneous: {
        userToken: string;
        hasNoSuccessModal?: boolean;
        onTokenExpired?: () => void;
    } | null = null
) => {
    try {
        const response = await apiClient.post(data.url, data.body, {
            headers: {
                Authorization: `${miscellaneous?.userToken}`,
                ...(data?.headers ?? {}),
            },
        });

        if (!miscellaneous?.hasNoSuccessModal) {
            handleSucess(response.data);
        }

        return response.data as ApiResponseTypes;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;

            if (status === 401 && error.response?.data?.expiredToken) {
                miscellaneous?.onTokenExpired?.();
            }

            throw new Error(JSON.stringify(error.response?.data));
        }

        throw error;
    }
};

const put = async (
  data: { url: string; body?: unknown | null; params?: unknown; headers?: unknown | null },
  miscellaneous: {
      userToken: string;
      hasNoSuccessModal?: boolean;
      onTokenExpired?: () => void;
  } | null = null
) => {
    try {
        const response = await apiClient.put(data.url, data.body, {
            params: data.params,
            headers: {
                Authorization: `${miscellaneous?.userToken}`,
                ...(data?.headers ?? {}),
            },
        });

        if (!miscellaneous?.hasNoSuccessModal) {
            handleSucess(response.data);
        }

        return response.data as ApiResponseTypes;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;

            if (status === 401 && error.response?.data?.expiredToken) {
                miscellaneous?.onTokenExpired?.();
            }

            throw new Error(JSON.stringify(error.response?.data));
        }

        throw error;
    }
};

const remove = async (
    data: { url: string; params?: unknown; headers?: unknown | null },
    miscellaneous: {
        userToken: string;
        hasNoSuccessModal?: boolean;
        onTokenExpired?: () => void;
    } | null = null
) => {
    try {
        const response = await apiClient.delete(data.url, {
            params: data.params,
            headers: {
                Authorization: `${miscellaneous?.userToken}`,
                ...(data?.headers ?? {}),
            },
        });

        if (!miscellaneous?.hasNoSuccessModal) {
            handleSucess(response.data);
        }

        return response.data as ApiResponseTypes;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;
            if (status === 401 && error.response?.data?.expiredToken) {
                miscellaneous?.onTokenExpired?.();
            }
            throw new Error(JSON.stringify(error.response?.data));
        }
        throw error;
    }
};

const handleError = (
    errorStr: string | unknown,  // Accepte aussi des erreurs non-string
    miscelleanous?: { form?: unknown; hasNoFailureModal?: boolean }
) => {
    try {
        console.log(errorStr)
        // 1. Gestion des cas où errorStr n'est pas une string
        if (typeof errorStr !== 'string') {
            console.error('Erreur non-string reçue:', errorStr);

            if (!miscelleanous?.hasNoFailureModal) {
                toast.error("Erreur inconnue", ToastHelpers.UI.ERROR);
            }
            return null;
        }

        // 2. Parsing sécurisé du JSON
        let error: any;
        try {
            error = JSON.parse(errorStr);
        } catch (parseError) {
            console.error('Échec du parsing JSON:', errorStr);

            if (!miscelleanous?.hasNoFailureModal) {
                toast.error(
                    errorStr.includes('Invalid time value')
                        ? "Format de date invalide"
                        : "Erreur technique",
                    ToastHelpers.UI.ERROR
                );
            }
            return null;
        }

        // 3. Gestion des erreurs 401 (Non autorisé)
        if (error?.status === '401') {
            useModalStore.getState().setShowModal(true);
            return null;
        }

        // 4. Affichage du message d'erreur
        if (!miscelleanous?.hasNoFailureModal) {
            toast.error(
                error?.message || "Erreur inconnue",
                {
                    ...ToastHelpers.UI.ERROR,
                    description: Array.isArray(error?.data)
                        ? renderErrorList(error.data)
                        : error?.data?.message || undefined,
                }
            );
        }

        // 5. Gestion des erreurs de formulaire
        if (miscelleanous?.form && error?.errors) {
            Object.keys(error.errors).forEach((tag) => {
                (miscelleanous.form as UseFormReturn).setError(tag, {
                    type: 'manual',
                    message: Array.isArray(error.errors[tag])
                        ? error.errors[tag].join(', ')
                        : error.errors[tag],
                });
            });
        }

    } catch (globalError) {
        console.error('Erreur dans handleError:', globalError);
        toast.error('Échec critique', ToastHelpers.UI.ERROR);
    }

    return null;
};

const handleSucess = (responseData: { message?: string }) => {
    if (responseData?.message) {
        toast.success(
            responseData.message ?? 'Modification effectuée avec succès',
            ToastHelpers.UI.SUCCESS
        );
    }
};

const apiService = { get, post,put,remove,handleError };

export default apiService;
