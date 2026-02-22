package oj.pojo.entity;
import lombok.Data;

@Data
public class Result {
        private Integer code;
        private String message;
        private Object data;



        public static Result success(){
                Result result = new Result();
                result.code = 1;
                result.message = "success";
                return result;
        }
        public static Result success(Object object) {
                Result result = new Result();
                result.code = 1;
                result.message = "success";
                result.data = object;
                return result;
        }

        public static Result error(String msg) {
                Result result = new Result();
                result.message = msg;
                result.code = 0;
                return result;
        }

//        自定义错误码和信息
        public static Result defined(Integer code, String msg, Object data) {
                Result result = new Result();
                result.message = msg;
                result.code = code;
                result.data = data;
                return result;
        }


}
