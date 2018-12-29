package com.willhains.purity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Indicates that a type has value semantics.
 *
 * Formally:
 * <ol>
 * <li>It is final, or all of its subtypes are guaranteed to have value semantics</li>
 * <li><b>It is strictly and deeply immutable</b>, and therefore inherently thread-safe</li>
 * <li>It has correct {@link Object#equals equals}, {@link Object#hashCode hashCode},
 *     and {@link Object#toString toString} implementations</li>
 * <li>It has no interaction with input/output whatsoever</li>
 * <li>It has no interaction with concurrency mechanisms whatsoever</li>
 * </ol>
 *
 * @author willhains
 */
@Retention(SOURCE)
@Target({TYPE, TYPE_PARAMETER, PARAMETER, TYPE_USE})
public @interface Value {}
