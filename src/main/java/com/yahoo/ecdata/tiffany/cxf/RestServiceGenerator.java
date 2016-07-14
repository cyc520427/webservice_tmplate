package com.yahoo.ecdata.tiffany.cxf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.BusWiringBeanFactoryPostProcessor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.yahoo.ecdata.tiffany.util.ReflectionUtils;

@Component("restfulServiceExporter")
public class RestServiceGenerator implements ApplicationContextAware {

	Logger LOG = Logger.getLogger(RestServiceGenerator.class);

	@SuppressWarnings("deprecation")
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		Map<String, Object> serviceBeans = appContext.getBeansWithAnnotation(WebService.class);
		Map<String, Object> providerBeans = appContext.getBeansWithAnnotation(Provider.class);

		for (String beanName : serviceBeans.keySet()) {

			Object serviceBean = serviceBeans.get(beanName);
			Class<?>[] interfaces = ReflectionUtils.getExportedInterface(beanName, serviceBean, WebService.class);

			if (interfaces.length == 0) {
				LOG.debug("bean " + beanName + " has no restful interfaces to export, ignore it");
				continue;
			}

			Class<?> restfulInterface = getRestfulInterface(interfaces);
			if (restfulInterface == null) {
				LOG.debug("bean " + beanName + " has no suitable restful interface, choose a default one.");
				restfulInterface = interfaces[0];
			}

			Method[] methods = restfulInterface.getMethods();
			if (methods.length == 0) {
				LOG.debug("bean " + beanName + " has no restful interfaces to export, ignore it");
				continue;
			}

			try {

				JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
				sf.setResourceClasses(restfulInterface);
				sf.setResourceProvider(restfulInterface, new SingletonResourceProvider(serviceBean));
				sf.setAddress("/" + getAddress(beanName));

				if (sf.getBus() == null) {
					Bus bus = BusFactory.getThreadDefaultBus();
					BusWiringBeanFactoryPostProcessor.updateBusReferencesInContext(bus, appContext);
					sf.setBus(bus);
				}

				List<Object> providerBeanList = new ArrayList<Object>(providerBeans.values());
				providerBeanList.add(new JacksonJsonProvider());
				sf.setProviders(providerBeanList);
				sf.create();
				LOG.info("Exported service " + beanName + " as restful web service");
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error("can't export bean '" + beanName + "' with interface '" + restfulInterface.getName() + "'",
						ex);
			}
		}
	}

	private String getAddress(String beanName) {
		if (beanName.endsWith("Service")) {
			return beanName.substring(0, beanName.indexOf("Service")).toLowerCase();
		} else {
			return beanName;
		}
	}

	private Class<?> getRestfulInterface(Class<?>[] interfaces) {
		if (interfaces == null || interfaces.length == 0) {
			return null;
		} else {
			return interfaces[interfaces.length - 1];
		}
	}
}
