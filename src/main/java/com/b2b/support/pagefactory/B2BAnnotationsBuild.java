package com.b2b.support.pagefactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;

import com.b2b.support.B2BBy;
import com.b2b.support.B2BFindAll;
import com.b2b.support.B2BFindBy;
import com.b2b.support.B2BFindBys;
import com.b2b.support.B2BHow;
import com.b2b.support.ParameterProvider;
import com.b2b.support.TestDataPropertiesEnum;

public class B2BAnnotationsBuild {
	private Field field;

	  public B2BAnnotationsBuild(Field field) {
	    this.field = field;
	  }

	  public boolean isLookupCached() {
	    return (field.getAnnotation(CacheLookup.class) != null);
	  }

	  public By buildBy() {
	    assertValidAnnotations();

	    By ans = null;

	    B2BFindBys findBys = field.getAnnotation(B2BFindBys.class);
	    if (findBys != null) {
	      ans = buildByFromFindBys(findBys);
	    }

	    B2BFindAll findAll = field.getAnnotation(B2BFindAll.class);
	    if (ans == null && findAll != null) {
	      ans = buildBysFromFindByOneOf(findAll);
	    }

	    B2BFindBy findBy = field.getAnnotation(B2BFindBy.class);
	    if (ans == null && findBy != null) {
	      ans = buildByFromFindBy(findBy);
	    }

	    if (ans == null) {
	      ans = buildByFromDefault();
	    }

	    if (ans == null) {
	      throw new IllegalArgumentException("Cannot determine how to locate element " + field);
	    }

	    return ans;
	  }

	  protected By buildByFromDefault() {
	    return new ByIdOrName(field.getName());
	  }
	  /**
	   * Build the B2BFindBys 
	   * @param findBys
	   * @return
	   */
	  protected By buildByFromFindBys(B2BFindBys findBys) {
	    assertValidFindBys(findBys);

	    B2BFindBy[] findByArray = findBys.value();
	    By[] byArray = new By[findByArray.length];
	    for (int i = 0; i < findByArray.length; i++) {
	      byArray[i] = buildByFromFindBy(findByArray[i]);
	    }

	    return new ByChained(byArray);
	  }
	  /**
	   * Build the B2BFindAll
	   * @param findBys
	   * @return
	   */
	  protected By buildBysFromFindByOneOf(B2BFindAll findBys) {
	    assertValidFindAll(findBys);

	    B2BFindBy[] findByArray = findBys.value();
	    By[] byArray = new By[findByArray.length];
	    for (int i = 0; i < findByArray.length; i++) {
	      byArray[i] = buildByFromFindBy(findByArray[i]);
	    }

	    return new ByAll(byArray);
	  }
	  /**
	   * Build the B2BFind
	   * @param findBy
	   * @return
	   */
	  protected By buildByFromFindBy(B2BFindBy findBy) {
	    assertValidFindBy(findBy);

	    By ans = buildByFromShortFindBy(findBy);
	    if (ans == null) {
	      ans = buildByFromLongFindBy(findBy);
	    }

	    return ans;
	  }
	  /**
	   * proceesing and get the element from xpath file
	   * @param input
	   * @return
	   */
	  private static String processParameter(String input) {
	        Pattern p = Pattern.compile("\\{(.+?)\\}");
	        Matcher m = p.matcher(input);
	        String result = input;
	        while (m.find()) {
	            String fullMatch = m.group();
	            String propName = m.group(1);
	            String propValue = ParameterProvider.getInstance().getParameter(TestDataPropertiesEnum.XPATHS.toString(), propName);
	            if (propValue == null) {
	                throw new IllegalArgumentException("Cannot find property: " + propName);
	            }
	            result = result.replace(fullMatch, propValue);
	        }
	        return result;
	    }
	  /**
	   * Build the B2BFind
	   * @param findBy
	   * @return
	   */
	  protected By buildByFromLongFindBy(B2BFindBy findBy) {
	    B2BHow how = findBy.how();
	    String using = findBy.using();

	    switch (how) {
	      case CLASS_NAME:
	        return B2BBy.className(processParameter(using));

	      case CSS:
	        return B2BBy.cssSelector(processParameter(using));

	      case ID:
	        return B2BBy.id(processParameter(using));

	      case ID_OR_NAME:
	        return new ByIdOrName(using);

	      case LINK_TEXT:
	        return B2BBy.linkText(processParameter(using));

	      case NAME:
	        return B2BBy.name(processParameter(using));

	      case PARTIAL_LINK_TEXT:
	        return By.partialLinkText(using);

	      case TAG_NAME:
	        return B2BBy.tagName(processParameter(using));
	      case XPATH:
	      {
	    	  System.out.println(By.xpath(using)+"in annotation -------------------------------------");
	        return B2BBy.xpath(processParameter(using));
	      }
	      default:
	        // Note that this shouldn't happen (eg, the above matches all
	        // possible values for the How enum)
	        throw new IllegalArgumentException("Cannot determine how to locate element " + field);
	    }
	  }
	  /**
	   * Build the B2BFind
	   * @param findBy
	   * @return
	   */
	  protected By buildByFromShortFindBy(B2BFindBy findBy) {
	    if (!"".equals(findBy.className()))
	    {
	    	return B2BBy.className(processParameter(findBy.className()));
	    }
	    if (!"".equals(findBy.css()))
	      return B2BBy.cssSelector(processParameter(findBy.css()));

	    if (!"".equals(findBy.id()))
	      return B2BBy.id(processParameter(findBy.id()));

	    if (!"".equals(findBy.linkText()))
	      return B2BBy.linkText(processParameter(findBy.linkText()));

	    if (!"".equals(findBy.name()))
	      return B2BBy.name(findBy.name());

	    if (!"".equals(findBy.partialLinkText()))
	      return B2BBy.partialLinkText(findBy.partialLinkText());

	    if (!"".equals(findBy.tagName()))
	      return B2BBy.tagName(processParameter(findBy.tagName()));

	    if (!"".equals(findBy.xpath()))
	      return B2BBy.xpath(processParameter(findBy.xpath()));

	    return null;
	  }
	  /**
	   * validate the B2BAnnotations
	   */
	  private void assertValidAnnotations() {
	    B2BFindBys findBys = field.getAnnotation(B2BFindBys.class);
	    B2BFindAll findAll = field.getAnnotation(B2BFindAll.class);
	    B2BFindBy findBy = field.getAnnotation(B2BFindBy.class);
	    if (findBys != null && findBy != null) {
	      throw new IllegalArgumentException("If you use a '@B2BFindBys' annotation, " +
	           "you must not also use a '@B2BFindBy' annotation");
	    }
	    if (findAll != null && findBy != null) {
	      throw new IllegalArgumentException("If you use a '@B2BFindAll' annotation, " +
	           "you must not also use a '@B2BFindBy' annotation");
	    }
	    if (findAll != null && findBys != null) {
	      throw new IllegalArgumentException("If you use a '@B2BFindAll' annotation, " +
	           "you must not also use a '@B2BFindBys' annotation");
	    }
	  }
	  /**
	   * validate the B2BFindBys
	   * @param findBys
	   */
	  private void assertValidFindBys(B2BFindBys findBys) {
	    for (B2BFindBy findBy : findBys.value()) {
	      assertValidFindBy(findBy);
	    }
	  }
	  /**
	   * validate the B2BFindAll
	   * @param findBys
	   */
	  
	  private void assertValidFindAll(B2BFindAll findBys) {
	    for (com.b2b.support.B2BFindBy findBy : findBys.value()) {
	      assertValidFindBy(findBy);
	    }
	  }
	  /**
	   * validate the B2BFindBy
	   * @param findBy
	   */
	  private void assertValidFindBy(B2BFindBy findBy) {
	    if (findBy.how() != null) {
	      if (findBy.using() == null) {
	        throw new IllegalArgumentException(
	            "If you set the 'how' property, you must also set 'using'");
	      }
	    }

	    Set<String> finders = new HashSet<String>();
	    if (!"".equals(findBy.using())) finders.add("how: " + findBy.using());
	    if (!"".equals(findBy.className())) finders.add("class name:" + findBy.className());
	    if (!"".equals(findBy.css())) finders.add("css:" + findBy.css());
	    if (!"".equals(findBy.id())) finders.add("id: " + findBy.id());
	    if (!"".equals(findBy.linkText())) finders.add("link text: " + findBy.linkText());
	    if (!"".equals(findBy.name())) finders.add("name: " + findBy.name());
	    if (!"".equals(findBy.partialLinkText()))
	      finders.add("partial link text: " + findBy.partialLinkText());
	    if (!"".equals(findBy.tagName())) finders.add("tag name: " + findBy.tagName());
	    if (!"".equals(findBy.xpath())) finders.add("xpath: " + findBy.xpath());
	    
	    // A zero count is okay: it means to look by name or id.
	    if (finders.size() > 1) {
	      throw new IllegalArgumentException(
	          String.format("You must specify at most one location strategy. Number found: %d (%s)",
	              finders.size(), finders.toString()));
	    }
	  }
}
