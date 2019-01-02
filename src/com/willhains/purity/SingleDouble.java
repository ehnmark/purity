package com.willhains.purity;

import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.willhains.purity.DoubleRule.*;
import static java.util.Objects.requireNonNull;

/** A primitive `double` version of {@link Single}. */
public abstract @Value class SingleDouble<This extends SingleDouble<This>> implements SingleComparable<This>
{
	// The single-argument constructor of the subclass
	private final DoubleFunction<? extends This> _constructor;
	
	/**
	 * The raw underlying value. This property should be used only when passing the underlying value to
	 * external APIs. As much as possible, use the wrapped value type.
	 */
	public final double raw;
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 */
	protected SingleDouble(final double rawValue, final DoubleFunction<? extends This> constructor)
	{
		raw = requireNonNull(rawValue);
		_constructor = requireNonNull(constructor);
	}
	
	/**
	 * @param rawValue The raw, immutable value this object will represent.
	 * @param constructor A method reference to the constructor of the implementing subclass.
	 * @param rules Validation and data normalisation rules for the raw underlying value.
	 */
	protected SingleDouble(final double rawValue, final DoubleFunction<? extends This> constructor, final DoubleRule rules)
	{
		this(rules.apply(rawValue), constructor);
	}
	
	@Override public final int hashCode() { return Double.hashCode(raw); }
	@Override public String toString() { return Double.toString(raw); }
	
	@Override
	public final boolean equals(final Object other)
	{
		if(other == this) return true;
		if(other == null) return false;
		if(!this.getClass().equals(other.getClass())) return false;
		@SuppressWarnings("unchecked") final This that = (This)other;
		return this.raw == that.raw;
	}
	
	/** Rule to trap non-numbers: NaN, infinity. */
	public static DoubleRule realNumber = rules(
		validUnless(Double::isNaN, "Not a number"),
		validOnlyIf(Double::isFinite, "Must be finite"));
	
	/** Generate rule to allow only raw integer values greater than or equal to `minValue`. */
	public static DoubleRule min(final double minValue)
	{
		return validUnless(raw -> raw < minValue, raw -> raw + " < " + minValue);
	}
	
	/** Generate rule to allow only raw integer values less than or equal to `maxValue`. */
	public static DoubleRule max(final double maxValue)
	{
		return validUnless(raw -> raw > maxValue, raw -> raw + " > " + maxValue);
	}
	
	/** Generate rule to allow only raw integer values greater than (but not equal to) `lowerBound`. */
	public static DoubleRule greaterThan(final double lowerBound)
	{
		return validOnlyIf(raw -> raw > lowerBound, raw -> raw + " <= " + lowerBound);
	}
	
	/** Generate rule to allow only raw integer values less than (but not equal to) `upperBound`. */
	public static DoubleRule lessThan(final double upperBound)
	{
		return validOnlyIf(raw -> raw < upperBound, raw -> raw + " >= " + upperBound);
	}
	
	/** Generate rule to normalise the raw double value to a minimum floor value. */
	public static DoubleRule floor(final double minValue) { return raw -> Math.max(raw, minValue); }
	
	/** Generate rule to normalise the raw double value to a maximum ceiling value. */
	public static DoubleRule ceiling(final double maxValue) { return raw -> Math.min(raw, maxValue); }
	
	@Override public final int compareTo(final This that) { return Double.compare(this.raw, that.raw); }
	
	public final This round() { return map(Math::round); }
	public final This roundUp() { return map(Math::ceil); }
	public final This roundDown() { return map(Math::floor); }
	
	/**
	 * Test the raw value with {@code condition}.
	 * This method is useful when using {@link Optional#filter} or {@link Stream#filter}.
	 * <pre>
	 * optional.filter(x -&gt; x.is($ -> $ > 0))
	 * </pre>
	 *
	 * @param condition a {@link Predicate} that tests the raw value type.
	 * @return {@code true} if the underlying {@link #raw} value satisfies {@code condition};
	 *         {@code false} otherwise.
	 */
	public final boolean is(final DoublePredicate condition) { return condition.test(raw); }
	
	/** Reverse of {@link #is(DoublePredicate)}. */
	public final boolean isNot(final DoublePredicate condition) { return !is(condition); }
	
	/**
	 * Test the raw value by {@code condition}.
	 *
	 * @return an {@link Optional} containing this instance if the condition is met; empty otherwise.
	 * @see #is(DoublePredicate)
	 */
	public final Optional<This> filter(final DoublePredicate condition)
	{
		@SuppressWarnings("unchecked") final This self = (This)this;
		return Optional.of(self).filter(it -> it.is(condition));
	}
	
	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return A new instance of this type.
	 */
	public final This map(final DoubleUnaryOperator mapper)
	{
		final double mapped = mapper.applyAsDouble(raw);
		@SuppressWarnings("unchecked") final This self = (This)this;
		if(mapped == raw) return self;
		return _constructor.apply(mapped);
	}
	
	/**
	 * Construct a new value of this type with the raw underlying value converted by {@code mapper}.
	 *
	 * @param mapper The mapping function to apply to the raw underlying value.
	 * @return The value returned by {@code mapper}.
	 */
	public final This flatMap(final DoubleFunction<? extends This> mapper) { return mapper.apply(raw); }
}
