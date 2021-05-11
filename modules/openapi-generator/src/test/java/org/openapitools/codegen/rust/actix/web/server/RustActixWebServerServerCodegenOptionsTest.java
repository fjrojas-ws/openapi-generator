package org.openapitools.codegen.rust.actix.web.server;

import org.openapitools.codegen.AbstractOptionsTest;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.languages.RustActixWebServerServerCodegen;
import org.openapitools.codegen.options.RustActixWebServerServerCodegenOptionsProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RustActixWebServerServerCodegenOptionsTest extends AbstractOptionsTest {
    private RustActixWebServerServerCodegen codegen = mock(RustActixWebServerServerCodegen.class, mockSettings);

    public RustActixWebServerServerCodegenOptionsTest() {
        super(new RustActixWebServerServerCodegenOptionsProvider());
    }

    @Override
    protected CodegenConfig getCodegenConfig() {
        return codegen;
    }

    @SuppressWarnings("unused")
    @Override
    protected void verifyOptions() {
        // TODO: Complete options using Mockito
        // verify(codegen).someMethod(arguments)
    }
}

