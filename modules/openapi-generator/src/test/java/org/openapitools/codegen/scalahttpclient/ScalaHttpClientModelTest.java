package org.openapitools.codegen.scalahttpclient;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.languages.ScalaHttpClientCodegen;

import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.languages.ScalaHttpClientCodegen;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.parser.util.SchemaTypeUtil;


import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("static-method")
public class ScalaHttpClientModelTest {

    @Test(description = "convert a simple scala model")
    public void simpleModelTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperties("name", new StringSchema())
                .addProperties("createdAt", new DateTimeSchema())
                .addRequiredItem("id")
                .addRequiredItem("name");
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "a sample model");
        Assert.assertEquals(cm.vars.size(), 3);

        final CodegenProperty property1 = cm.vars.get(0);
        Assert.assertEquals(property1.baseName, "id");
        Assert.assertEquals(property1.getter, "getId");
        Assert.assertEquals(property1.setter, "setId");
        Assert.assertEquals(property1.datatype, "Long");
        Assert.assertEquals(property1.name, "id");
        Assert.assertNull(property1.defaultValue);
        Assert.assertEquals(property1.baseType, "Long");
        Assert.assertTrue(property1.hasMore);
        Assert.assertTrue(property1.required);
        Assert.assertTrue(property1.isNotContainer);

        final CodegenProperty property2 = cm.vars.get(1);
        Assert.assertEquals(property2.baseName, "name");
        Assert.assertEquals(property2.getter, "getName");
        Assert.assertEquals(property2.setter, "setName");
        Assert.assertEquals(property2.datatype, "String");
        Assert.assertEquals(property2.name, "name");
        Assert.assertNull(property2.defaultValue);
        Assert.assertEquals(property2.baseType, "String");
        Assert.assertTrue(property2.hasMore);
        Assert.assertTrue(property2.required);
        Assert.assertTrue(property2.isNotContainer);

        final CodegenProperty property3 = cm.vars.get(2);
        Assert.assertEquals(property3.baseName, "createdAt");
        Assert.assertEquals(property3.getter, "getCreatedAt");
        Assert.assertEquals(property3.setter, "setCreatedAt");
        Assert.assertEquals(property3.datatype, "Date");
        Assert.assertEquals(property3.name, "createdAt");
        Assert.assertNull(property3.defaultValue);
        Assert.assertEquals(property3.baseType, "Date");
        Assert.assertFalse(property3.hasMore);
        Assert.assertFalse(property3.required);
        Assert.assertTrue(property3.isNotContainer);
    }

    @Test(description = "convert a model with list property")
    public void listPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("id", new IntegerSchema().format(SchemaTypeUtil.INTEGER64_FORMAT))
                .addProperties("urls", new ArraySchema()
                        .items(new StringSchema()))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "a sample model");
        //Assert.assertEquals(cm.vars.size(), 2);

        final CodegenProperty property1 = cm.vars.get(1);
        Assert.assertEquals(property1.baseName, "urls");
        Assert.assertEquals(property1.getter, "getUrls");
        Assert.assertEquals(property1.setter, "setUrls");
        Assert.assertEquals(property1.datatype, "List[String]");
        Assert.assertEquals(property1.name, "urls");
        Assert.assertEquals(property1.defaultValue, "new ListBuffer[String]() ");
        Assert.assertEquals(property1.baseType, "List");
        Assert.assertEquals(property1.containerType, "array");
        Assert.assertFalse(property1.required);
        Assert.assertTrue(property1.isContainer);
    }

    @Test(description = "convert a model with a map property")
    public void mapPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("translations", new MapSchema()
                        .additionalProperties(new StringSchema()))
                .addRequiredItem("id");
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "a sample model");
        Assert.assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        Assert.assertEquals(property1.baseName, "translations");
        Assert.assertEquals(property1.getter, "getTranslations");
        Assert.assertEquals(property1.setter, "setTranslations");
        Assert.assertEquals(property1.datatype, "Map[String, String]");
        Assert.assertEquals(property1.name, "translations");
        Assert.assertEquals(property1.defaultValue, "new HashMap[String, String]() ");
        Assert.assertEquals(property1.baseType, "Map");
        Assert.assertEquals(property1.containerType, "map");
        Assert.assertFalse(property1.required);
        Assert.assertTrue(property1.isContainer);
    }

    @Test(description = "convert a model with complex properties")
    public void complexPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("children", new Schema().$ref("#/definitions/Children"));
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "a sample model");
        Assert.assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        Assert.assertEquals(property1.baseName, "children");
        Assert.assertEquals(property1.getter, "getChildren");
        Assert.assertEquals(property1.setter, "setChildren");
        Assert.assertEquals(property1.datatype, "Children");
        Assert.assertEquals(property1.name, "children");
        Assert.assertNull(property1.defaultValue);
        Assert.assertEquals(property1.baseType, "Children");
        Assert.assertFalse(property1.required);
        Assert.assertTrue(property1.isNotContainer);
    }

    @Test(description = "convert a model with complex list property")
    public void complexListPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("children", new ArraySchema()
                        .items(new Schema().$ref("#/definitions/Children")));
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "a sample model");
        Assert.assertEquals(cm.vars.size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        Assert.assertEquals(property1.baseName, "children");
        Assert.assertEquals(property1.complexType, "Children");
        Assert.assertEquals(property1.getter, "getChildren");
        Assert.assertEquals(property1.setter, "setChildren");
        Assert.assertEquals(property1.datatype, "List[Children]");
        Assert.assertEquals(property1.name, "children");
        Assert.assertEquals(property1.defaultValue, "new ListBuffer[Children]() ");
        Assert.assertEquals(property1.baseType, "List");
        Assert.assertEquals(property1.containerType, "array");
        Assert.assertFalse(property1.required);
        Assert.assertTrue(property1.isContainer);
    }

    @Test(description = "convert a model with complex map property")
    public void complexMapPropertyTest() {
        final Schema model = new Schema()
                .description("a sample model")
                .addProperties("children", new MapSchema()
                        .additionalProperties(new Schema().$ref("#/definitions/Children")));
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "a sample model");
        Assert.assertEquals(cm.vars.size(), 1);
        Assert.assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("Children")).size(), 1);

        final CodegenProperty property1 = cm.vars.get(0);
        Assert.assertEquals(property1.baseName, "children");
        Assert.assertEquals(property1.complexType, "Children");
        Assert.assertEquals(property1.getter, "getChildren");
        Assert.assertEquals(property1.setter, "setChildren");
        Assert.assertEquals(property1.datatype, "Map[String, Children]");
        Assert.assertEquals(property1.name, "children");
        Assert.assertEquals(property1.defaultValue, "new HashMap[String, Children]() ");
        Assert.assertEquals(property1.baseType, "Map");
        Assert.assertEquals(property1.containerType, "map");
        Assert.assertFalse(property1.required);
        Assert.assertTrue(property1.isContainer);
        Assert.assertFalse(property1.isNotContainer);
    }

    @Test(description = "convert an array model")
    public void arrayModelTest() {
        final Schema schema = new ArraySchema()
                .items(new Schema().$ref("#/definitions/Children"))
                .description("an array model");
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", schema);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "an array model");
        Assert.assertEquals(cm.vars.size(), 0);
        Assert.assertEquals(cm.parent, "ListBuffer[Children]");
        Assert.assertEquals(cm.imports.size(), 2);
        Assert.assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("ListBuffer", "Children")).size(), 2);
    }

    @Test(description = "convert an map model")
    public void mapModelTest() {
        final Schema model = new Schema()
                .description("a map model")
                .additionalProperties(new Schema().$ref("#/definitions/Children"));
        final DefaultCodegen codegen = new ScalaHttpClientCodegen();
        final CodegenModel cm = codegen.fromModel("sample", model);

        Assert.assertEquals(cm.name, "sample");
        Assert.assertEquals(cm.classname, "Sample");
        Assert.assertEquals(cm.description, "a map model");
        Assert.assertEquals(cm.vars.size(), 0);
        Assert.assertEquals(cm.parent, "HashMap[String, Children]");
        Assert.assertEquals(cm.imports.size(), 2);
        Assert.assertEquals(Sets.intersection(cm.imports, Sets.newHashSet("HashMap", "Children")).size(), 2);
    }
}