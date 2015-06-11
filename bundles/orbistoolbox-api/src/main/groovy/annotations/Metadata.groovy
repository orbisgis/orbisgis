package annotations

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by sylvain on 11/06/15.
 */

@Retention(RetentionPolicy.RUNTIME)
@interface Metadata {
        String title()
        String linkType() default "simple"
        String role()
        String href()
}