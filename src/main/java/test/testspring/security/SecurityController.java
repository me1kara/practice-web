//package test.testspring.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import test.testspring.domain.Member;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/security")
//public class SecurityController {
//    @Autowired
//    private SecurityService securityService;
//
////    @GetMapping("/create/token")
////    public Map<String, Object> createToken(@RequestParam(value="subject") String subject){
////        String token = securityService.createToken(subject, (2*1000*60));
////        Map<String, Object> map = new LinkedHashMap<>();
////        map.put("result", token);
////
////        return map;
////    }
//
////    @GetMapping("/create/subject")
////    public Map<String, Object> getSubject(@RequestParam(value="token") String token){
////        Member subject = securityService.getMember(token);
////        Map<String, Object> map = new LinkedHashMap<>();
////        map.put("result", subject);
////        return map;
////    }
//
//}
