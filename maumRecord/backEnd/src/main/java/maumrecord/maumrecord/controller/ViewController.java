//package maumrecord.maumrecord.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
////todo: 프론트 병합 시 사용되는 포워딩 -> 필요 시 주석 해제
//@Controller
//public class ViewController {
//    // API나 정적 리소스를 제외한 모든 경로를 index.html로 포워딩
//    @RequestMapping(value = {"/{path:^(?!api|static|swagger-ui|v3).*$}", "/**/{path:^(?!api|static|swagger-ui|v3).*$}"})
//    public String forward() {
//        return "forward:/index.html";
//    }
//}
