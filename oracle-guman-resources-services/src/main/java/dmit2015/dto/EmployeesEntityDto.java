package dmit2015.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeesEntityDto {

    private Integer employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    private Integer salary;

    private String jobTitle;
    private String departmentName;
    private String managerName;
}
