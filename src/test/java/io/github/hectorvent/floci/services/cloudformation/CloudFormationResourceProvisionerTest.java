package io.github.hectorvent.floci.services.cloudformation;

import io.github.hectorvent.floci.services.cloudformation.model.StackResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudFormationResourceProvisionerTest {

    private CloudFormationResourceProvisioner provisioner;

    @BeforeEach
    void setUp() {
        // Services are null — unsupported types never invoke them
        provisioner = new CloudFormationResourceProvisioner(
                null, null, null, null, null, null,
                null, null, null, null, null, null, null);
    }

    @Test
    void customResourceType_stubbedWithCreateComplete() {
        StackResource resource = provisioner.provision(
                "MyService", "Acorns::ECS::ServiceV2", null,
                null, "us-east-1", "000000000000", "test-stack");

        assertEquals("CREATE_COMPLETE", resource.getStatus());
        assertNull(resource.getStatusReason());
        assertTrue(resource.getPhysicalId().startsWith("MyService-"));
        assertEquals("arn:aws:stub:::MyService", resource.getAttributes().get("Arn"));
    }

    @Test
    void unsupportedAwsResourceType_stubbedWithCreateComplete() {
        StackResource resource = provisioner.provision(
                "MyBus", "AWS::Events::EventBus", null,
                null, "us-east-1", "000000000000", "test-stack");

        assertEquals("CREATE_COMPLETE", resource.getStatus());
        assertNull(resource.getStatusReason());
        assertTrue(resource.getPhysicalId().startsWith("MyBus-"));
        assertEquals("arn:aws:stub:::MyBus", resource.getAttributes().get("Arn"));
    }

    @Test
    void multipleCustomTypes_eachGetUniquePhysicalId() {
        StackResource r1 = provisioner.provision(
                "ResourceA", "Custom::MyThing", null,
                null, "us-east-1", "000000000000", "test-stack");
        StackResource r2 = provisioner.provision(
                "ResourceB", "Custom::MyThing", null,
                null, "us-east-1", "000000000000", "test-stack");

        assertNotEquals(r1.getPhysicalId(), r2.getPhysicalId());
        assertEquals("CREATE_COMPLETE", r1.getStatus());
        assertEquals("CREATE_COMPLETE", r2.getStatus());
    }
}
