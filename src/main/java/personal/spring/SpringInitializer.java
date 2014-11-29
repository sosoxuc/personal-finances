package personal.spring;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpringInitializer extends
	AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
	return new Class[] {};
    }

    @Override
    protected Filter[] getServletFilters() {
	
	CharacterEncodingFilter encoder = new CharacterEncodingFilter();
	encoder.setEncoding("UTF-8");
	encoder.setForceEncoding(Boolean.TRUE);
	return new Filter[] { encoder };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
	return new Class[] { SpringConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
	return new String[] { "/rest/*" };
    }
}