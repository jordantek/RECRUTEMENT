package  com.tpc.tpcgestpaie.localapp.dto.users;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateOrUpdateDTO {

    private Long userId; // si présent → update
    private Long employeId;
    private Long companyId;
    private String username;
    private String password; // facultatif pour update
    private String email;
    private List<Long> roleIds;
    private String status = "active";
    private List<SuperieurDTO> superieurs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuperieurDTO {
        private Long contratSuperieurId;
        private Integer ordre;
    }
}
