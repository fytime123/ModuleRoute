package com.liufuyi.routecompiler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.liufuyi.routecompiler.Route")
@SupportedOptions("moduleName")
public class RouterProcessor extends AbstractProcessor {

    //记录模块名
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Map<String, String> options = processingEnvironment.getOptions();
        moduleName = options.get("moduleName");

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {


        Messager messager = processingEnv.getMessager();
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (elementsAnnotatedWith == null || elementsAnnotatedWith.isEmpty()) {
            return false;
        }

        List<PathClass> pathClasses = new ArrayList<>();
        for (Element element : elementsAnnotatedWith) {

            PathClass value = new PathClass();

            value.path = element.getAnnotation(Route.class).value();
            value.clazz = element.toString();
            pathClasses.add(value);

            messager.printMessage(Diagnostic.Kind.NOTE, "-------------");
            messager.printMessage(Diagnostic.Kind.NOTE, value.clazz);
        }


        if (pathClasses.isEmpty()) {
            return false;
        }

        String outputPackage = "com.liufuyi.route.output";

        StringBuilder builder = new StringBuilder();
        //自动生成类的源代码
        String start = "package " + outputPackage + ";\n" +
                "\n" +
                "import android.app.Activity;\n" +
                "\n" +
                "import com.liufuyi.route.api.Router;\n" +
                "\n" +
                "import com.liufuyi.route.api.RouterLoad;\n" +
                "\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class " + moduleName.toUpperCase() + "Route implements RouterLoad {\n" +
                "    @Override\n" +
                "    public void load() {\n";

        builder.append(start);

        for (PathClass value : pathClasses) {
            builder.append("         Router.getInstance().register(" + "\"" + value.path + "\"" + "," + "\"" + value.clazz + "\"" + ");\n");
        }

        String end = "    }\n" +
                "}";
        builder.append(end);

        String code = builder.toString();


        //通过环境获取当前Filer，具有操作文件功能，避免写死路径
        Filer filer = processingEnv.getFiler();
        try {
            //因为有一个注解就执行一次process，所以同一个模块下有标志文件就不需要再创建了
            if (new File(outputPackage + "." + moduleName.toUpperCase() + "Route").exists()) {
                return false;
            }
            JavaFileObject sourceFile = filer.createSourceFile(outputPackage + "." + moduleName.toUpperCase() + "Route");
            OutputStream outputStream = sourceFile.openOutputStream();
            outputStream.write(code.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    static class PathClass {
        String path;
        String clazz;
    }
}
