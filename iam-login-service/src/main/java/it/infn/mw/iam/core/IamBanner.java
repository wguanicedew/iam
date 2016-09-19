package it.infn.mw.iam.core;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.ResourceBanner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;


public class IamBanner extends ResourceBanner {

  public IamBanner(Resource resource) {
    super(resource);
  }


  protected PropertyResolver getIamInfoPropertyResolver() throws IOException {

    MutablePropertySources sources = new MutablePropertySources();

    ResourcePropertySource iamVersionProps =
        new ResourcePropertySource(new ClassPathResource("iam.version.properties"));
    ResourcePropertySource gitCommitProps =
        new ResourcePropertySource(new ClassPathResource("git.properties"));
    sources.addLast(iamVersionProps);
    sources.addLast(gitCommitProps);


    return new PropertySourcesPropertyResolver(sources);

  }

  @Override
  protected List<PropertyResolver> getPropertyResolvers(Environment environment,
      Class<?> sourceClass) {

    List<PropertyResolver> resolvers = super.getPropertyResolvers(environment, sourceClass);

    try {
      resolvers.add(getIamInfoPropertyResolver());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return resolvers;
  }

}
