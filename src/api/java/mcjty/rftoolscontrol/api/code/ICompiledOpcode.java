package mcjty.rftoolsbase.api.control.code;

import mcjty.rftoolsbase.api.control.parameters.Parameter;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Representation of a compiled opcode
 */
public interface ICompiledOpcode {

    @Nonnull
    List<Parameter> getParameters();
}
