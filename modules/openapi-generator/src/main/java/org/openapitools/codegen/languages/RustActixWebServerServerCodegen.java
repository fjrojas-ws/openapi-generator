package org.openapitools.codegen.languages;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.features.*;
import org.openapitools.codegen.utils.ModelUtils;
import org.openapitools.codegen.utils.URLPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapitools.codegen.utils.StringUtils.camelize;
import static org.openapitools.codegen.utils.StringUtils.underscore;

public class RustActixWebServerServerCodegen extends RustServerCodegen implements CodegenConfig {
    public static final String PROJECT_NAME = "projectName";

    static final Logger LOGGER = LoggerFactory.getLogger(RustActixWebServerServerCodegen.class);

    private HashMap<String, String> modelXmlNames = new HashMap<String, String>();

    private static final String NO_FORMAT = "%%NO_FORMAT";

    protected String apiVersion = "1.0.0";
    protected String serverHost = "localhost";
    protected int serverPort = 8080;
    protected String projectName = "openapi-server";
    protected String apiPath = "rust-actix-web-server";
    protected String apiDocPath = "docs/";
    protected String modelDocPath = "docs/";
    protected String packageName;
    protected String packageVersion;
    protected String externCrateName;
    protected Map<String, Map<String, String>> pathSetMap = new HashMap();
    protected Map<String, Map<String, String>> callbacksPathSetMap = new HashMap();

    private static final String uuidType = "uuid::Uuid";
    private static final String bytesType = "swagger::ByteArray";

    private static final String xmlMimeType = "application/xml";
    private static final String textXmlMimeType = "text/xml";
    private static final String octetMimeType = "application/octet-stream";
    private static final String plainTextMimeType = "text/plain";
    private static final String jsonMimeType = "application/json";

    // RFC 7386 support
    private static final String mergePatchJsonMimeType = "application/merge-patch+json";

    // RFC 7807 Support
    private static final String problemJsonMimeType = "application/problem+json";
    private static final String problemXmlMimeType = "application/problem+xml";


    public CodegenType getTag() {
        return CodegenType.SERVER;
    }

    public String getName() {
        return "rust-actix-web-server";
    }

    public String getHelp() {
        return "Generates a rust-actix-web-server server.";
    }

    public RustActixWebServerServerCodegen() {
        super();

        //apiPackage = "Apis";
        //modelPackage = "Models";

        // Show the generation timestamp by default
        //// hideGenerationTimestamp = Boolean.FALSE;

        // set the output folder here
        outputFolder = "generated-code" + File.separator + "rust-actix-web-server";

        /*
         * Models.  You can write model files using the modelTemplateFiles map.
         * if you want to create one template for file, you can do so here.
         * for multiple files for model, just put another entry in the `modelTemplateFiles` with
         * a different extension
         */
        modelTemplateFiles.clear();
        // modelTemplateFiles.put("model.mustache", ".rs");

        /*
         * Api classes.  You can write classes for each Api file with the apiTemplateFiles map.
         * as with models, add multiple entries with different extensions for multiple files per
         * class
         */
        apiTemplateFiles.clear();
        //// apiTemplateFiles.put("api.mustache", ".rs");

        /*
         * Template Location.  This is the location which templates will be read from.  The generator
         * will use the resource stream to attempt to read the templates.
         */
        // embeddedTemplateDir = templateDir = "rust-server";
        embeddedTemplateDir = templateDir = "rust-actix-web-server";


        importMapping = new HashMap<String, String>();

        cliOptions.clear();
        cliOptions.add(new CliOption(CodegenConstants.PACKAGE_NAME,
                "Rust crate name (convention: snake_case).")
                .defaultValue("openapi_client"));
        cliOptions.add(new CliOption(CodegenConstants.PACKAGE_VERSION,
                "Rust crate version."));

        /*
         * Additional Properties.  These values can be passed to the templates and
         * are available in models, apis, and supporting files
         */
        additionalProperties.put("apiVersion", apiVersion);
        additionalProperties.put("apiPath", apiPath);

        /*
         * Supporting Files.  You can write single files for the generator with the
         * entire object tree available.  If the input file has a suffix of `.mustache
         * it will be processed by the template engine.  Otherwise, it will be copied
         */
         supportingFiles.clear();

//        supportingFiles.add(new SupportingFile("openapi.mustache", "api", "openapi.yaml"));
        supportingFiles.add(new SupportingFile("Cargo.mustache", "", "Cargo.toml"));
//        supportingFiles.add(new SupportingFile("cargo-config", ".cargo", "config"));
//        supportingFiles.add(new SupportingFile("gitignore", "", ".gitignore"));
//        supportingFiles.add(new SupportingFile("lib.mustache", "src", "lib.rs"));
//        supportingFiles.add(new SupportingFile("context.mustache", "src", "context.rs"));
        supportingFiles.add(new SupportingFile("api_error.mustache", "src", "api_error.rs"));
        supportingFiles.add(new SupportingFile("db.mustache", "src", "db.rs"));
        supportingFiles.add(new SupportingFile("main.mustache", "src", "main.rs"));
        supportingFiles.add(new SupportingFile("schema.mustache", "src", "schema.rs"));
        supportingFiles.add(new SupportingFile("test.mustache", "src", "test.rs"));

        supportingFiles.add(new SupportingFile("model.mustache", "src/api", "model.rs"));
        supportingFiles.add(new SupportingFile("mod.mustache", "src/api", "mod.rs"));
        supportingFiles.add(new SupportingFile("routes.mustache", "src/api", "routes.rs"));
        supportingFiles.add(new SupportingFile("tests.mustache", "src/api", "tests.rs"));

//        supportingFiles.add(new SupportingFile("header.mustache", "src", "header.rs"));
//        supportingFiles.add(new SupportingFile("server-mod.mustache", "src/server", "mod.rs"));
//        supportingFiles.add(new SupportingFile("client-mod.mustache", "src/client", "mod.rs"));
//        supportingFiles.add(new SupportingFile("example-server-main.mustache", "examples/server", "main.rs"));
//        supportingFiles.add(new SupportingFile("example-server-server.mustache", "examples/server", "server.rs"));
//        supportingFiles.add(new SupportingFile("example-client-main.mustache", "examples/client", "main.rs"));
//        supportingFiles.add(new SupportingFile("example-ca.pem", "examples", "ca.pem"));
//        supportingFiles.add(new SupportingFile("example-server-chain.pem", "examples", "server-chain.pem"));
//        supportingFiles.add(new SupportingFile("example-server-key.pem", "examples", "server-key.pem"));

        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md")
                .doNotOverwrite());

    }
}
