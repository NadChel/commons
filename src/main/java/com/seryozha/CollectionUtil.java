package com.seryozha;

import java.util.Collection;

public class CollectionUtil {
    public static <T> CollectionModificationBuilder<T> clear(Collection<T> targetCollection) {
        return new CollectionModificationBuilder<>(targetCollection);
    }

    public static class CollectionModificationBuilder<T> {
        private final Collection<T> targetCollection;

        private CollectionModificationBuilder(Collection<T> targetCollection) {
            this.targetCollection = targetCollection;
        }

        public void andFillFrom(Collection<? extends T> sourceCollection) {
            targetCollection.clear();
            targetCollection.addAll(sourceCollection);
        }
    }
}
