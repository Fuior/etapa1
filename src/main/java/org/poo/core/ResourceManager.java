package org.poo.core;

import org.poo.fileio.CommandInput;

public interface ResourceManager {

    void add(CommandInput commandInput);
    void delete(CommandInput commandInput);
}
