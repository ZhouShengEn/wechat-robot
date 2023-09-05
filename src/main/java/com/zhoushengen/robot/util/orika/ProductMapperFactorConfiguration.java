package com.zhoushengen.robot.util.orika;

import com.zhoushengen.robot.util.orika.convert.String2IntConvert;
import org.springframework.context.annotation.Configuration;

/**
 * @author :zhoushengen
 * @date : 2022/8/2
 */
@Configuration
public class ProductMapperFactorConfiguration extends AbstractMapperFactoryConfiguration {

    @Override
    protected void addFluidMapper() {

    }

    @Override
    protected void addConverter() {
        getMapperFactory().getConverterFactory().registerConverter("string2IntConvert", new String2IntConvert());

    }
}
