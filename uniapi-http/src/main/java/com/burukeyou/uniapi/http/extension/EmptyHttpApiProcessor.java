package com.burukeyou.uniapi.http.extension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author caizhihao
 */

@Slf4j
@Component
public class EmptyHttpApiProcessor implements HttpApiProcessor<Annotation> {

}
