package org.springframework.boot.context.properties;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Provides an access to {@link ConversionServiceDeducer} to retrieve {@link ConversionService}.
 */
@RequiredArgsConstructor
public class ConversionServiceRetriever {

    private final ApplicationContext applicationContext;

    public ConversionService getConversionService() {
        return new ComposedConversionService(
            new ConversionServiceDeducer(this.applicationContext).getConversionServices());
    }

    @RequiredArgsConstructor
    private class ComposedConversionService implements ConversionService {

        private final List<ConversionService> conversionServices;

        @Override
        public boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
            return conversionServices.stream()
                .anyMatch(cs -> cs.canConvert(sourceType, targetType));
        }

        @Override
        public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
            return conversionServices.stream()
                .anyMatch(cs -> cs.canConvert(sourceType, targetType));
        }

        @Override
        public <T> T convert(@Nullable Object source, Class<T> targetType) {
            Assert.notNull(targetType, "Target type to convert to cannot be null");
            return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType));
        }

        @Override
        public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
            Assert.notNull(targetType, "Target type to convert to cannot be null");
            return conversionServices.stream()
                .filter(cs -> cs.canConvert(sourceType, targetType))
                .findAny()
                .map(cs -> cs.convert(source, sourceType, targetType))
                .orElseThrow(() -> new ConverterNotFoundException(sourceType, targetType));
        }
    }
}
