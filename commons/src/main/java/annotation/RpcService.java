package annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author DearAhri520
 * <p>
 * 该注解只能使用在服务类上,当需要注册一个新的rpc服务时,使用该注解注册
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
@Documented
public @interface RpcService {
}
