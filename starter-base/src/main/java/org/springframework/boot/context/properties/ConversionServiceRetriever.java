package org.springframework.boot.context.properties;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;

@RequiredArgsConstructor
public class ConversionServiceRetriever {

    private final ApplicationContext applicationContext;

    public ConversionService getConversionService() {
        return new ConversionServiceDeducer(this.applicationContext).getConversionService();
    }
}
