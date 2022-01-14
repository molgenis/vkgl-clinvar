package org.molgenis.vkgl.clinvar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vkgl.clinvar.model.Settings;
import org.springframework.util.ResourceUtils;

@ExtendWith(MockitoExtension.class)
class GenesFilterTest {
  private GenesFilter genesFilter;

  @BeforeEach
  void setUp() throws FileNotFoundException {
    Path pseudogenesPath = ResourceUtils.getFile("classpath:pseudogenes.txt").toPath();
    Settings settings = Settings.builder().pseudogenesPath(pseudogenesPath).build();
    this.genesFilter = new GenesFilter(settings);
  }
  @Test
  void filter() {
    assertEquals("",genesFilter.filter("ABC23"));
  }

  @Test
  void filterNonPseudo() {
    assertEquals("TEST", genesFilter.filter("TEST"));
  }
}