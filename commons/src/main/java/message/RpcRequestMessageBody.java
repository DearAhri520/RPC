package message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import protocol.MessageType;

import java.util.Arrays;

/**
 * @author DearAhri520
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class RpcRequestMessageBody extends MessageBody {
    /**
     * 调用的接口全限定名，服务端根据它找到实现
     */
    private String interfaceName;

    /**
     * 调用接口中的方法名
     */
    private String methodName;

    /**
     * 方法返回类型
     */
    private Class<?> returnType;

    /**
     * 方法参数类型数组
     */
    private Class<?>[] parameterTypes;

    /**
     * 方法参数值数组
     */
    private Object[] parameterValue;

    public RpcRequestMessageBody(String interfaceName, String methodName, Class<?>[] parameterTypes, Object[] parameterValue, Class<?> returnType) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
        this.returnType = returnType;
    }
}