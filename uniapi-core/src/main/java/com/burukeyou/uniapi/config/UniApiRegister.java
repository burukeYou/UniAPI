package com.burukeyou.uniapi.config;

import java.lang.annotation.Annotation;
import java.util.List;

public interface UniApiRegister {

    List<Class<? extends Annotation>> register();
}
