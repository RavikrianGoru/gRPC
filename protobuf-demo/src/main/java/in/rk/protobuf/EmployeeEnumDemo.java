package in.rk.protobuf;

import in.rk.models.EmpGrade;
import in.rk.models.Employee;

public class EmployeeEnumDemo {
    public static void main(String[] args) {
        Employee e1= Employee.newBuilder()
                .setName("Raju")
                .setEmpGrade(EmpGrade.AVP)
                .build();
        System.out.println(e1);
    }
}
