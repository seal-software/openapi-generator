package org.openapitools.codegen.languages;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.servers.Server;
import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.SupportingFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AsciidocGenerator extends DefaultCodegen {
    protected String invokerPackage = "org.openapitools.client";
    protected String groupId = "org.openapitools";
    protected String artifactId = "openapi-client";
    protected String artifactVersion = "1.0.0";

    public AsciidocGenerator() {
        super();
        outputFolder = "asciidoc";
        embeddedTemplateDir = templateDir = "asciidoc";

        defaultIncludes = new HashSet<String>();

        cliOptions.add(new CliOption("appName", "short name of the application"));
        cliOptions.add(new CliOption("appDescription", "description of the application"));
        cliOptions.add(new CliOption("infoUrl", "a URL where users can get more information about the application"));
        cliOptions.add(new CliOption("infoEmail", "an email address to contact for inquiries about the application"));
        cliOptions.add(new CliOption("licenseInfo", "a short description of the license"));
        cliOptions.add(new CliOption("licenseUrl", "a URL pointing to the full license"));
        cliOptions.add(new CliOption(CodegenConstants.INVOKER_PACKAGE, CodegenConstants.INVOKER_PACKAGE_DESC));
        cliOptions.add(new CliOption(CodegenConstants.GROUP_ID, CodegenConstants.GROUP_ID_DESC));
        cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_ID, CodegenConstants.ARTIFACT_ID_DESC));
        cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_VERSION, CodegenConstants.ARTIFACT_VERSION_DESC));

        additionalProperties.put("appName", "OpenAPI Sample");
        additionalProperties.put("appDescription", "A sample OpenAPI server");
        //additionalProperties.put("infoUrl", "https://openapi-generator.tech");
        //additionalProperties.put("infoEmail", "team@openapitools.org");
        //additionalProperties.put("licenseInfo", "All rights reserved");
        //additionalProperties.put("licenseUrl", "http://apache.org/licenses/LICENSE-2.0.html");
        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        additionalProperties.put(CodegenConstants.GROUP_ID, groupId);
        additionalProperties.put(CodegenConstants.ARTIFACT_ID, artifactId);
        additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);

        supportingFiles.add(new SupportingFile("index.mustache", "", "index.adoc"));
        supportingFiles.add(new SupportingFile("overview.mustache", "", "overview.adoc"));
        supportingFiles.add(new SupportingFile("operations.mustache", "", "operations.adoc"));
        supportingFiles.add(new SupportingFile("models.mustache", "", "models.adoc"));

        reservedWords = new HashSet<String>();

        languageSpecificPrimitives = new HashSet<String>();
        importMapping = new HashMap<String, String>();
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.DOCUMENTATION;
    }

    @Override
    public String getName() {
        return "asciidoc";
    }

    @Override
    public String getHelp() {
        return "Generates a Asciidoc files.";
    }


    @Override
    public String escapeUnsafeCharacters(String input) {
        return input;
    }

    @Override
    public String escapeQuotationMark(String input) {
        return input;
    }

    /*
        @Override
        public String getTypeDeclaration(Schema p) {
            if (ModelUtils.isArraySchema(p)) {
                ArraySchema ap = (ArraySchema) p;
                Schema inner = ap.getItems();
                return getSchemaType(p) + "[" + getTypeDeclaration(inner) + "]";
            } else if (ModelUtils.isMapSchema(p)) {
                Schema inner = ModelUtils.getAdditionalProperties(p);
                return getSchemaType(p) + "[String, " + getTypeDeclaration(inner) + "]";
            }
            return super.getTypeDeclaration(p);
        }
    */
    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, List<Server> servers) {
        CodegenOperation op = super.fromOperation(path, httpMethod, operation, servers);
        if (op.returnType != null) {
            op.returnType = normalizeType(op.returnType);
        }

        //path is an unescaped variable in the mustache template api.mustache line 82 '<&path>'
        op.path = sanitizePath(op.path);
        op.vendorExtensions.put("x-codegen-httpMethodUpperCase", httpMethod.toUpperCase(Locale.ROOT));
        op.vendorExtensions.put("x-codegen-operationIdAsSentence", operationIdAsSentence(op));

        if (op.examples != null) {
            for (Map<String, String> example : op.examples) {
                String type = resolveSourceType(example);
                if (type != null) {
                    example.put("x-codegen-sourceType", type);
                }
            }
        }

        appendShouldIncludeProperty(op.vendorExtensions);

        return op;
    }

    private void appendShouldIncludeProperty(Map<String, Object> vExtensions) {
        String noDoc = (String) vExtensions.get("x-no-doc");

        if (vExtensions.containsKey("x-no-doc") || noDoc != null && noDoc.equalsIgnoreCase("true")) {
            vExtensions.put("x-codegen-shouldInclude", false);
        } else {
            vExtensions.put("x-codegen-shouldInclude", true);
        }
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {

        if (property.required ||
                property.minimum != null ||
                property.maximum != null ||
                property.minLength != null ||
                property.maxLength != null ||
                property.minItems != null ||
                property.maxItems != null ||
                property.pattern != null
        ) {
            property.vendorExtensions.put("x-codegen-hasConstraints", true);
        } else {
            property.vendorExtensions.put("x-codegen-hasConstraints", false);
        }

        appendShouldIncludeProperty(property.vendorExtensions);
        property.vendorExtensions.put("x-codegen-hasExample", hasExmpample(property.example));
    }

    @Override
    public void postProcessParameter(CodegenParameter parameter) {

        if (parameter.required ||
                parameter.minimum != null ||
                parameter.minimum != null ||
                parameter.maximum != null ||
                parameter.minLength != null ||
                parameter.maxLength != null ||
                parameter.minItems != null ||
                parameter.maxItems != null ||
                parameter.pattern != null
        ) {
            parameter.vendorExtensions.put("x-codegen-hasConstraints", true);
        } else {
            parameter.vendorExtensions.put("x-codegen-hasConstraints", false);
        }

        appendShouldIncludeProperty(parameter.vendorExtensions);
        parameter.vendorExtensions.put("x-codegen-hasExample", hasExmpample(parameter.example));
    }

    private boolean hasExmpample(String example) {
        return example != null && !example.equals("null");
    }

    // Resolve asciidoc valid source types
    public String resolveSourceType(Map<String, String> example) {
        final String ct = example.getOrDefault("contentType", "");
        final String type = ct.replaceAll("^.+\\/.*(json|xml|yaml|css|html|js|javascript).*$|.*$", "$1");
        return type.isEmpty() ? "text" : type;
    }

    private String operationIdAsSentence(CodegenOperation op) {
        String str = op.operationId.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2").toLowerCase(Locale.ENGLISH);
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }

    private String sanitizePath(String p) {
        //prefer replace a ', instead of a fuLL URL encode for readability
        return p.replaceAll("'", "%27");
    }


    /**
     * Normalize type by wrapping primitive types with single quotes.
     *
     * @param type Primitive type
     * @return Normalized type
     */
    public String normalizeType(String type) {
        return type.replaceAll("\\b(Boolean|Integer|Number|String|Date)\\b", "'$1'");
    }
}
