package slimbok;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("slimbok.Setter")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SetterProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Setter.class)) {
            if (element.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) element;
                TypeElement classElement = (TypeElement) field.getEnclosingElement();
                String setterCode = generateSetterCode(field);
                writeSetterMethod(classElement, setterCode);
            }
        }
        return true;
    }

    private String generateSetterCode(VariableElement field) {
        String fieldName = field.getSimpleName().toString();
        String fieldType = field.asType().toString();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return "public void " + methodName + "(" + fieldType + " " + fieldName + ") { this." + fieldName + " = " + fieldName + "; }";
    }

    private void writeSetterMethod(TypeElement classElement, String setterCode) {
        String className = classElement.getQualifiedName().toString();
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className + "Generated", classElement);
            try (Writer writer = sourceFile.openWriter()) {
                writer.write("package " + processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName() + ";\n");
                writer.write("public class " + className + "Generated {\n");
                writer.write(setterCode + "\n");
                writer.write("}\n");
            }
        } catch (IOException e) {
            // Handle I/O exceptions
        }
    }
}

