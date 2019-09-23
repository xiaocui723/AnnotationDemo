package com.example.libico;

import com.example.libannotation.FindDeviceInfo;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class IocProcessor extends AbstractProcessor {
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(FindDeviceInfo.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FindDeviceInfo.class);
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process invoked");
        //一、收集信息
        for (Element element : elements) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, element.toString());
            //检查element类型
            if (!checkAnnotationValid(element)) {
                return false;
            }
            //field type
            mMessager.printMessage(Diagnostic.Kind.NOTE, "1");
            ExecutableElement executableElement = (ExecutableElement) element;
            mMessager.printMessage(Diagnostic.Kind.NOTE, "2");
            //class type
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();//TypeElement

            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        "DeviceInfo",
                        typeElement);
                Writer writer = jfo.openWriter();
                writer.write(generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                error(typeElement,
                        "Unable to write injector for type %s: %s",
                        typeElement, e.getMessage());
            }
        }
        return true;
    }

    private boolean checkAnnotationValid(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            error(annotatedElement, "%s must be declared on field.", FindDeviceInfo.class.getSimpleName());
            return false;
        }
        if (ClassValidator.isPrivate(annotatedElement)) {
            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
            return false;
        }

        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }

    private String generateJavaCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.annotationdemo;\n\n");
        sb.append("public class DeviceInfo");
        sb.append(" {\n");
        generateMethods(sb);
        sb.append("}");
        return sb.toString();
    }


    private void generateMethods(StringBuilder sb) {
        sb.append("public void sayHello() {\n");
        sb.append("System.out.println(\"hello\");");
        sb.append("}\n");
    }
}
