package org.custom.tool;

import java.io.File;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.impl.AddonManagerImpl;
import org.jboss.forge.furnace.manager.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.se.FurnaceFactory;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

public class Main
{

   public static void main(String[] args)
   {
      Furnace furnace = FurnaceFactory.getInstance();
      furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(OperatingSystemUtils.getUserForgeDir(), "addons"));
      furnace.startAsync();

      AddonManager manager = new AddonManagerImpl(furnace, new MavenAddonDependencyResolver());

      AddonId projects = AddonId.from("org.jboss.forge.addon:projects", "3.0.0.Alpha3");
      AddonId maven = AddonId.from("org.jboss.forge.addon:maven", "3.0.0.Alpha3");

      manager.install(projects).perform();
      manager.install(maven).perform();

      AddonRegistry registry = furnace.getAddonRegistry();
      Addons.waitUntilStarted(registry.getAddon(projects));
      Addons.waitUntilStarted(registry.getAddon(maven));

      ProjectFactory factory = registry.getServices(ProjectFactory.class).get();
      Project project = factory.createTempProject();

      DependencyFacet dependencies = project.getFacet(DependencyFacet.class);
      dependencies.addDirectDependency(DependencyBuilder.create("org.jboss.forge.furnace:furnace-se:2.22.9.Final"));
   }
}
