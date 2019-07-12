package org.openapitools.codegen.languages;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.io.IOUtils;
import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.CodegenResponse;
import org.openapitools.codegen.CodegenSecurity;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.SupportingFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

public class StaticHtmlSealGenerator extends DefaultCodegen {

    protected String invokerPackage = "org.openapitools.client";
    protected String groupId = "org.openapitools";
    protected String artifactId = "openapi-client";
    protected String artifactVersion = "1.0.0";

    public StaticHtmlSealGenerator() throws IOException, URISyntaxException {
        super();
        outputFolder = "html-seal";
        embeddedTemplateDir = templateDir = "html-seal";

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
        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        additionalProperties.put(CodegenConstants.GROUP_ID, groupId);
        additionalProperties.put(CodegenConstants.ARTIFACT_ID, artifactId);
        additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);

        supportingFiles.add(new SupportingFile("index.mustache", "", "index.html"));
        supportingFiles.add(new SupportingFile("nav.mustache", "", "nav.html"));
        supportingFiles.add(new SupportingFile("param.mustache", "", "param.html"));
        supportingFiles.add(new SupportingFile("constraints.mustache", "", "constraints.html"));
        supportingFiles.add(new SupportingFile("data-type.mustache", "", "data-type.html"));
        supportingFiles.add(new SupportingFile("response.mustache", "", "response.html"));
        supportingFiles.add(new SupportingFile("overview.mustache", "", "overview.html"));
        supportingFiles.add(new SupportingFile("operations.mustache", "", "operations.html"));
        supportingFiles.add(new SupportingFile("models.mustache", "", "models.html"));
        supportingFiles.add(new SupportingFile("style.css.mustache", "", "style.css"));

        reservedWords = new HashSet<String>();

        languageSpecificPrimitives.clear();
        languageSpecificPrimitives.add("byte");
        languageSpecificPrimitives.add("short");
        languageSpecificPrimitives.add("Short");
        languageSpecificPrimitives.add("int");
        languageSpecificPrimitives.add("integer");
        languageSpecificPrimitives.add("Integer");
        languageSpecificPrimitives.add("long");
        languageSpecificPrimitives.add("Long");
        languageSpecificPrimitives.add("float");
        languageSpecificPrimitives.add("Float");
        languageSpecificPrimitives.add("double");
        languageSpecificPrimitives.add("Double");
        languageSpecificPrimitives.add("boolean");
        languageSpecificPrimitives.add("Boolean");
        languageSpecificPrimitives.add("char");
        languageSpecificPrimitives.add("Char");
        languageSpecificPrimitives.add("String");
        languageSpecificPrimitives.add("string");
        languageSpecificPrimitives.add("file");
        languageSpecificPrimitives.add("Date");
        languageSpecificPrimitives.add("DateTime");
        languageSpecificPrimitives.add("List");
        languageSpecificPrimitives.add("Map");
        languageSpecificPrimitives.add("object");
        languageSpecificPrimitives.add("Object");

        importMapping = new HashMap<String, String>();
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.DOCUMENTATION;
    }

    @Override
    public String getName() {
        return "html-seal";
    }

    @Override
    public String getHelp() {
        return "Generates a Seal flavoured html files.";
    }

    @Override
    public String escapeUnsafeCharacters(String input) {
        return input;
    }

    @Override
    public String escapeQuotationMark(String input) {
        return input;
    }

    @Override
    public void preprocessOpenAPI(OpenAPI openAPI) {
        super.preprocessOpenAPI(openAPI);


        String url = (String) additionalProperties.get("logoUrl");
        try {
            Path path = url != null ? Paths.get(url) : Paths.get(getClass().getResource("/html-seal/OpenAPI_Logo.png").toURI());
            String logo = Base64.getEncoder().encodeToString(Files.readAllBytes(path));
            additionalProperties.put("logoBase64", logo);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load log from " + url, e);
        }
    }


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

        appendDeprecationFlag(op, operation);

        op.hasAuthMethods = op.authMethods != null && op.authMethods.size() > 0 && op.authMethods.stream().anyMatch(cs -> hasScopes.test(cs));

        appendShouldIncludeProperty(op.vendorExtensions);
        return op;
    }

    @Override
    public List<CodegenSecurity> fromSecurity(Map<String, SecurityScheme> securitySchemeMap) {
        List<CodegenSecurity> list = super.fromSecurity(securitySchemeMap);

        for (CodegenSecurity cs : list) {
            cs.hasScopes = hasScopes.test(cs);
        }
        return list;

    }

    private Predicate<CodegenSecurity> hasScopes = codegenSecurity -> {
        return codegenSecurity.scopes != null && codegenSecurity.scopes.size() > 0;
    };


    private void appendDeprecationFlag(CodegenOperation op, Operation operation) {
        if (operation.getParameters() != null) {
            op.allParams.forEach(p -> {
                operation.getParameters().stream()
                        .filter(param -> p.baseName.equals(param.getName()))
                        .findFirst()
                        .ifPresent(param -> p.vendorExtensions.put("x-codegen-deprecated", param.getDeprecated()));
            });
        }
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

        appendShouldIncludeProperty(model.vendorExtensions);
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

        addParameterInProperty(parameter);

        if (languageSpecificPrimitives.contains(parameter.dataType)) {
            parameter.isPrimitiveType = true;
            parameter.baseType = parameter.dataType.toLowerCase();
            parameter.isContainer = parameter.isMapContainer || parameter.isListContainer;
        }

        appendShouldIncludeProperty(parameter.vendorExtensions);
        parameter.vendorExtensions.put("x-codegen-hasExample", hasExmpample(parameter.example));
    }

    private void addParameterInProperty(CodegenParameter parameter) {
        if (parameter.isBodyParam) {
            parameter.vendorExtensions.put("x-codegen-paramIn", "body");
        } else if (parameter.isCookieParam) {
            parameter.vendorExtensions.put("x-codegen-paramIn", "cookie");
        } else if (parameter.isFormParam) {
            parameter.vendorExtensions.put("x-codegen-paramIn", "form");
        } else if (parameter.isPathParam) {
            parameter.vendorExtensions.put("x-codegen-paramIn", "path");
        } else if (parameter.isQueryParam) {
            parameter.vendorExtensions.put("x-codegen-paramIn", "query");
        } else if (parameter.isHeaderParam) {
            parameter.vendorExtensions.put("x-codegen-paramIn", "header");
        }
    }

    @Override
    public CodegenResponse fromResponse(String responseCode, ApiResponse response) {
        CodegenResponse resp = super.fromResponse(responseCode, response);
        //resp.primitiveType = languageSpecificPrimitives.contains(resp.dataType);
        return resp;
    }

    @Override
    public CodegenModel fromModel(String name, Schema schema) {
        CodegenModel model = super.fromModel(name, schema);
        model.vendorExtensions.put("x-codegen-deprecated", schema.getDeprecated());
        return model;
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
