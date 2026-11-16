package vn.iotstar.util;

public class Constant {
    public static final String SESSION_ACCOUNT = "account";
    public static final String SESSION_PENDING_EMAIL = "pendingVerifyEmail";
    public static final String SESSION_SUCCESS = "successMsg";
    public static final String COOKIE_REMEMBER = "remember_user";
    public static final String DIR = "D:\\upload";

    public static class Path {
        public static final String LOGIN = "/views/login.jsp";
        public static final String REGISTER = "/views/register.jsp";
        public static final String VERIFY_OTP = "/views/verify-otp.jsp";
        public static final String HOME = "/views/home.jsp";
        public static final String ADMIN_HOME = "/views/admin/home.jsp";
        public static final String MANAGER_HOME = "/views/manager/home.jsp";
        public static final String CATEGORY_LIST = "/views/admin/category-list.jsp";
        public static final String CATEGORY_ADD = "/views/admin/category-add.jsp";
        public static final String CATEGORY_EDIT = "/views/admin/category-edit.jsp";
    }
}
