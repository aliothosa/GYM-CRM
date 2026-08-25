package com.elioth.epam.workload.component;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/trainer-workload-component")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.elioth.epam.workload.component")
class CucumberComponentTest {
}
