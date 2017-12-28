package gov.va.ascent.maven.plugins.jsr303keygen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Locale;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.va.ascent.maven.plugins.jsr303keygen.testmodel.TestModel5;

public final class MessageInterpolator_UnitTest {

	private Annotation notNullAnnotation;
	private Annotation patternAnnotation;
	private Annotation sizeAnnotation;

	@Before
	public void before() {
		notNullAnnotation = getAnnotation(TestModel5.class, "stringValue1", NotNull.class);
		patternAnnotation = getAnnotation(TestModel5.class, "stringValue1", Pattern.class);
		sizeAnnotation = getAnnotation(TestModel5.class, "stringValue1", Size.class);
		Assert.assertNotNull(notNullAnnotation);
		Assert.assertNotNull(patternAnnotation);
		Assert.assertNotNull(sizeAnnotation);
	}

	@Test
	public void testInterpolate_with_DefaultMessageBundles() {
		final MessageInterpolator messageInterpolator = new MessageInterpolator();
		Assert.assertEquals("This field may not be null, please fix this and try again!",
				messageInterpolator.interpolate(notNullAnnotation));
		Assert.assertEquals("Zis field mai not bé null, pleasé fix zis ét try again!",
				messageInterpolator.interpolate(notNullAnnotation, Locale.FRENCH));
		Assert.assertEquals("must match \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"", messageInterpolator.interpolate(patternAnnotation));
		Assert.assertEquals("doit respecter \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"",
				messageInterpolator.interpolate(patternAnnotation, Locale.FRENCH));
		Assert.assertEquals("size must be between 0 and 35", messageInterpolator.interpolate(sizeAnnotation));
		Assert.assertEquals("la taille doit être comprise entre 0 et 35", messageInterpolator.interpolate(sizeAnnotation, Locale.FRENCH));
	}

	@Test
	public void testInterpolate_with_SingleCustomMessageBundleAndCache() {
		final MessageInterpolator messageInterpolator = new MessageInterpolator(new PlatformResourceBundleLocator("MyCustomBundle"));
		// 1st hit of these messages
		Assert.assertEquals("may not be null", messageInterpolator.interpolate(notNullAnnotation));
		Assert.assertEquals("ne peut pas être nul", messageInterpolator.interpolate(notNullAnnotation, Locale.FRENCH));
		// repeate to hit the cache
		Assert.assertEquals("may not be null", messageInterpolator.interpolate(notNullAnnotation));
		Assert.assertEquals("ne peut pas être nul", messageInterpolator.interpolate(notNullAnnotation, Locale.FRENCH));

		Assert.assertEquals("must match \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"", messageInterpolator.interpolate(patternAnnotation));
		Assert.assertEquals("doit respecter \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"",
				messageInterpolator.interpolate(patternAnnotation, Locale.FRENCH));
		Assert.assertEquals("The size of this field is bonkers, it's all out of whack.  The real size must be between 0 and 35",
				messageInterpolator.interpolate(sizeAnnotation));
		Assert.assertEquals("Le size of zis field eez bonkairs, eet eez all oot of whak. La réahl size must bé between 0 and 35",
				messageInterpolator.interpolate(sizeAnnotation, Locale.FRENCH));
	}

	@Test
	public void testInterpolate_with_SingleCustomMessageBundleAndNoCache() {
		final MessageInterpolator messageInterpolator =
				new MessageInterpolator(new PlatformResourceBundleLocator("MyCustomBundle"), false);
		Assert.assertEquals("may not be null", messageInterpolator.interpolate(notNullAnnotation));
		Assert.assertEquals("ne peut pas être nul", messageInterpolator.interpolate(notNullAnnotation, Locale.FRENCH));
		Assert.assertEquals("must match \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"", messageInterpolator.interpolate(patternAnnotation));
		Assert.assertEquals("doit respecter \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"",
				messageInterpolator.interpolate(patternAnnotation, Locale.FRENCH));
		Assert.assertEquals("The size of this field is bonkers, it's all out of whack.  The real size must be between 0 and 35",
				messageInterpolator.interpolate(sizeAnnotation));
		Assert.assertEquals("Le size of zis field eez bonkairs, eet eez all oot of whak. La réahl size must bé between 0 and 35",
				messageInterpolator.interpolate(sizeAnnotation, Locale.FRENCH));
	}
	
	@Test
	public void testInterpolate_with_MultipleCustomMessageBundleAndNoCache() {
		LinkedHashSet<String> customMessageBundles = new LinkedHashSet<String>();
		customMessageBundles.add("MyCustomBundle2");
		customMessageBundles.add("MyCustomBundle");
		final MessageInterpolator messageInterpolator =  new MessageInterpolator(customMessageBundles);
		//comes from bundle2
		Assert.assertEquals("This field may not be null, please fix this and try again!", messageInterpolator.interpolate(notNullAnnotation));
		Assert.assertEquals("Zis field mai not bé null, pleasé fix zis ét try again!", messageInterpolator.interpolate(notNullAnnotation, Locale.FRENCH));
		
		//comes from default 
		Assert.assertEquals("must match \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"", messageInterpolator.interpolate(patternAnnotation));
		Assert.assertEquals("doit respecter \"^(?!.*[ ]{2}|[ ])[a-zA-Z0-9\\-' ]+$\"",
				messageInterpolator.interpolate(patternAnnotation, Locale.FRENCH));
		
		//comes from bundle 1
		Assert.assertEquals("The size of this field is bonkers, it's all out of whack.  The real size must be between 0 and 35",
				messageInterpolator.interpolate(sizeAnnotation));
		Assert.assertEquals("Le size of zis field eez bonkairs, eet eez all oot of whak. La réahl size must bé between 0 and 35",
				messageInterpolator.interpolate(sizeAnnotation, Locale.FRENCH));
	}

	@Test
	public void testNoInterpolate_with_DefaultMessageBundles() {
		// INTERFACE_ERROR_KEY_1='allo 'allo, mon good Monsieur Mademoisellé, zis eez ze mézaje fair intairfacé based airrair 1.
		// INTERFACE_ERROR_KEY_1=Hello, my good sir madam, this is the message for interface based error 1.
		// INTERFACE_ERROR_KEY_2=Hello, my good sir madam, this is the message for interface based error 2.
		// INTERFACE_ERROR_KEY_2='allo 'allo, mon good Monsieur Mademoisellé, zis eez ze mézaje fair intairfacé based airrair 2.

		final MessageInterpolator messageInterpolator = new MessageInterpolator();
		Assert.assertEquals("Hello, my good sir madam, this is the message for interface based error 1.",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_1_VALUE"));
		Assert.assertEquals("'allo 'allo, mon good Monsieur Mademoisellé, zis eez ze mézaje fair intairfacé based airrair 1.",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_1_VALUE", Locale.FRENCH));
		Assert.assertEquals("INTERFACE_ERROR_KEY_2_VALUE", messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_2_VALUE"));
		Assert.assertEquals("INTERFACE_ERROR_KEY_2_VALUE",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_2_VALUE", Locale.FRENCH));
	}

	@Test
	public void testNoInterpolate_with_SingleCustomMessageBundle() {
		final MessageInterpolator messageInterpolator = 
				new MessageInterpolator(new PlatformResourceBundleLocator("MyCustomBundle"));
		Assert.assertEquals("INTERFACE_ERROR_KEY_1_VALUE", messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_1_VALUE"));
		Assert.assertEquals("INTERFACE_ERROR_KEY_1_VALUE",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_1_VALUE", Locale.FRENCH));
		Assert.assertEquals("Hello, my good sir madam, this is the message for interface based error 2.",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_2_VALUE"));
		Assert.assertEquals("'allo 'allo, mon good Monsieur Mademoisellé, zis eez ze mézaje fair intairfacé based airrair 2.",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_2_VALUE", Locale.FRENCH));
	}
	
	@Test
	public void testNoInterpolate_with_MultipleCustomMessageBundle() {
		LinkedHashSet<String> customMessageBundles = new LinkedHashSet<String>();
		customMessageBundles.add("MyCustomBundle2");
		customMessageBundles.add("MyCustomBundle");
		final MessageInterpolator messageInterpolator =  new MessageInterpolator(customMessageBundles);
		Assert.assertEquals("INTERFACE_ERROR_KEY_1_VALUE", messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_1_VALUE"));
		Assert.assertEquals("INTERFACE_ERROR_KEY_1_VALUE",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_1_VALUE", Locale.FRENCH));
		
		//comes from bundle 2 overriding bundle 1
		Assert.assertEquals("OVERRIDE Hello, my good sir madam, this is the message for interface based error 2.",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_2_VALUE"));
		Assert.assertEquals("OVERRIDE 'allo 'allo, mon good Monsieur Mademoisellé, zis eez ze mézaje fair intairfacé based airrair 2.",
				messageInterpolator.noInterpolate("INTERFACE_ERROR_KEY_2_VALUE", Locale.FRENCH));
	} 

	@Test
	public void testLocalisedMessageEquals() {
		final MessageInterpolator.LocalisedMessage localisedMessageUs1 = new MessageInterpolator.LocalisedMessage("This is a US message", Locale.US);
		final MessageInterpolator.LocalisedMessage localisedMessageUs2 = new MessageInterpolator.LocalisedMessage("This is a US message", Locale.US);
		final MessageInterpolator.LocalisedMessage localisedMessageUs3 = new MessageInterpolator.LocalisedMessage("This is a US message NOT EQUAL", Locale.US);
		final MessageInterpolator.LocalisedMessage localisedMessageFr1 = new MessageInterpolator.LocalisedMessage("This is a FR message", Locale.FRENCH);
		final MessageInterpolator.LocalisedMessage localisedMessageFr2 = new MessageInterpolator.LocalisedMessage("This is a FR message", Locale.FRENCH);
		final MessageInterpolator.LocalisedMessage localisedMessageFr3 = new MessageInterpolator.LocalisedMessage("This is a FR message NOT EQUAL", Locale.FRENCH);

		Assert.assertFalse(localisedMessageUs1.equals(null));
		Assert.assertTrue(localisedMessageUs1.equals(localisedMessageUs1));
		Assert.assertTrue(localisedMessageUs1.equals(localisedMessageUs2));
		Assert.assertFalse(localisedMessageUs1.equals(localisedMessageUs3));
		Assert.assertFalse(localisedMessageUs1.equals(localisedMessageFr1));

		Assert.assertFalse(localisedMessageFr1.equals(null));
		Assert.assertTrue(localisedMessageFr1.equals(localisedMessageFr1));
		Assert.assertTrue(localisedMessageFr1.equals(localisedMessageFr2));
		Assert.assertFalse(localisedMessageFr1.equals(localisedMessageFr3));
		Assert.assertFalse(localisedMessageFr1.equals(localisedMessageUs1));
	}

	private Annotation getAnnotation(final Class clazz, final String fieldName, final Class annotation) {
		for (final Field field : FieldUtils.getAllFields(clazz)) {
			if (field.getName().equals(fieldName)) {
				for (final Annotation fldAnnotation : field.getDeclaredAnnotations()) {
					if (fldAnnotation.annotationType().isAssignableFrom(annotation)) {
						return fldAnnotation;
					}
				}
			}
		}
		return null;
	}

}
