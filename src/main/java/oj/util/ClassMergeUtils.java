package oj.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.springframework.util.ReflectionUtils.findField;

@Slf4j
public class ClassMergeUtils {
    // 将两个类的属性值合并到一个类中
    // <T>是泛型声明，T是返回值的类型
    // 关键改动1：移除 throws IllegalAccessException 声明
    public static <T> T merge(@NonNull Object source, @NonNull T target) {
        // 新增：运行期空值兜底（@NonNull是编译期检查，增加运行期校验更健壮）
        if (source == null) {
            throw new IllegalArgumentException("源对象（source）不能为空！");
        }
        if (target == null) {
            throw new IllegalArgumentException("目标对象（target）不能为空！");
        }

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        // 获取所有属性
        Field[] sourceFields = sourceClass.getDeclaredFields();

        for (Field sourceField : sourceFields) {
            try {
                // 新增：跳过静态字段（属于类，不拷贝）和final字段（无法赋值）
                if (Modifier.isStatic(sourceField.getModifiers()) || Modifier.isFinal(sourceField.getModifiers())) {
                    continue;
                }

                // 打开私有属性访问权限
                sourceField.setAccessible(true);
                String fieldName = sourceField.getName();
                Class<?> fieldType = sourceField.getType();
                // 获取字段的值
                Object fieldValue = sourceField.get(source);

                // 查找目标类中是否有相同名称的字段（复用Spring的findField，已支持父类）
                Field targetField = findField(targetClass, fieldName);
                if (targetField != null) {
                    // 必须字段数据类型一致
                    if (targetField.getType().equals(fieldType)) {
                        // 新增：跳过目标字段的final修饰
                        if (Modifier.isFinal(targetField.getModifiers())) {
                            continue;
                        }
                        targetField.setAccessible(true);
                        // 设置目标字段的值
                        targetField.set(target, fieldValue);
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("合并字段[{}]时发生访问异常", sourceField.getName(), e);
                throw new RuntimeException("属性合并失败：" + sourceField.getName(), e);
            } catch (Exception e) {
                log.error("合并字段[{}]时发生未知异常", sourceField.getName(), e);
                throw new RuntimeException("属性合并失败：" + sourceField.getName(), e);
            }
        }

        return target;
    }
}