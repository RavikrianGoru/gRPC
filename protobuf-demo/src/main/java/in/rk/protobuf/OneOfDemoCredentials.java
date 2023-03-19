package in.rk.protobuf;

import in.rk.models.Credentials;
import in.rk.models.EmailLogin;
import in.rk.models.PhoneLogin;

public class OneOfDemoCredentials {
    public static void main(String[] args)
    {
        EmailLogin emailLogin= EmailLogin.newBuilder()
                .setEmailId("nobody@gmail.com")
                .setPassword("admin123")
                .build();
        PhoneLogin phoneLogin= PhoneLogin.newBuilder()
                .setPhoneNo("1234567890")
                .setOtp("556644")
                .build();
        System.out.println(emailLogin);
        System.out.println(phoneLogin);
        Credentials cred= Credentials.newBuilder()
                .setEmailLogin(emailLogin)
                .setPhoneLogin(phoneLogin)//latest will be taken
                .build();

        login(cred);
    }
    private static void login(Credentials cred)
    {
        System.out.println("Mode Case: "+cred.getModeCase());
        System.out.println("cred.hasEmailLogin(): "+cred.hasEmailLogin());
        System.out.println("cred.hasPhoneLogin(): "+cred.hasPhoneLogin());
        System.out.println("cred.getEmailLogin(): "+cred.getEmailLogin());
        System.out.println("cred.getPhoneLogin(): "+cred.getPhoneLogin());
        switch(cred.getModeCase())
        {
            case EMAIL_LOGIN:
                System.out.println(cred.getModeCase()+" : "+cred.getEmailLogin());
                break;
            case PHONE_LOGIN:
                System.out.println(cred.getModeCase()+" : "+cred.getPhoneLogin());
                break;
            default:
                break;
        }
    }
}
