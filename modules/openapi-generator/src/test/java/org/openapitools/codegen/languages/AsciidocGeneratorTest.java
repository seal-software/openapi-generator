package org.openapitools.codegen.languages;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.TestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AsciidocGeneratorTest {

    @Test
    public void testAdditionalPropertiesFalse() {
        OpenAPI openAPI = TestUtils.createOpenAPI();
        final StaticHtmlGenerator codegen = new StaticHtmlGenerator();

        Schema schema = new ObjectSchema()
                .additionalProperties(false)
                .addProperties("id", new IntegerSchema())
                .addProperties("name", new StringSchema());
        codegen.setOpenAPI(openAPI);
        CodegenModel cm = codegen.fromModel("test", schema);
        Assert.assertNotNull(cm);
    }

    @Test
    public void testSpecWithoutSchema() throws Exception {
        final OpenAPI openAPI = TestUtils.parseSpec("src/test/resources/3_0/ping.yaml");

        final AsciidocGenerator codegen = new AsciidocGenerator();
        codegen.preprocessOpenAPI(openAPI);

        Assert.assertEquals(openAPI.getInfo().getTitle(), "ping test");
    }

    @Test
    public void testResolveSourceType() {
        AsciidocGenerator generator = new AsciidocGenerator();

        Assert.assertEquals("json", generator.resolveSourceType(ImmutableMap.of("contentType", "application/json")));
        Assert.assertEquals("xml", generator.resolveSourceType(ImmutableMap.of("contentType", "application/xml")));
        Assert.assertEquals("html", generator.resolveSourceType(ImmutableMap.of("contentType", "application/x-vendor-html-extension-1")));
        Assert.assertEquals("css", generator.resolveSourceType(ImmutableMap.of("contentType", "*/css")));
        Assert.assertEquals("text", generator.resolveSourceType(ImmutableMap.of("contentType", "application/x-vendor-extension-1")));
        Assert.assertEquals("text", generator.resolveSourceType(ImmutableMap.of("contentType", "application/*")));
        Assert.assertEquals("text", generator.resolveSourceType(ImmutableMap.of("contentType", "*/*")));

    }
}