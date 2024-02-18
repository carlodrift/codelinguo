package fr.unilim.codelinguo.cli;

import picocli.CommandLine.IVersionProvider;

public class DynamicVersionProvider implements IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        return new String[] { "CodeLinguo version " + VersionUtil.getVersion() };
    }
}
