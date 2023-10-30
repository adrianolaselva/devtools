package fixture.profiles;

import fixture.resources.IntegratedTestResource;
import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.List;

import static java.util.Collections.singletonList;

public class IntegrationTestProfile implements QuarkusTestProfile {

    @Override
    public String getConfigProfile() {
        return "test-integrated";
    }

    @Override
    public List<TestResourceEntry> testResources() {
        return singletonList(new TestResourceEntry(IntegratedTestResource.class));
    }
}
