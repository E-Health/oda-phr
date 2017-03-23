package fi.oda.phr.profiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Profile;

@TestProfile
@Profile("test")
@Retention(RetentionPolicy.RUNTIME)
@Target(
    { ElementType.TYPE, ElementType.METHOD })
public @interface TestProfile {
}
