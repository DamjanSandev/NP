package Napredno.Lab8.XML;

import java.util.*;
import java.util.stream.Collectors;


public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase == 1) {
            //TODO Print the component object
            System.out.println(component.print(""));
        } else if (testCase == 2) {
            //TODO print the composite object
            System.out.println(composite.print(""));
        } else if (testCase == 3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level", "1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level", "2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level", "3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            //TODO print the main object
            System.out.println(main.print(""));
        }
    }
}

interface XMLComponent {
    String print(String indent);

    void addAttribute(String key, String value);
}

abstract class XMLElement implements XMLComponent {
    String tagName;
    Map<String, String> attributes;

    public XMLElement(String tagName) {
        this.tagName = tagName;
        attributes = new LinkedHashMap<>();
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }
}

class XMLLeaf extends XMLElement {

    String value;

    public XMLLeaf(String tagName, String value) {
        super(tagName);
        this.value = value;
    }

    @Override
    public String print(String indent) {
        return String.format("%s<%s%s>%s</%s>",
                indent,
                tagName,
                attributes.entrySet().stream().map(entry -> String.format(" %s=\"%s\"", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining("")), value, tagName);

    }
}

class XMLComposite extends XMLElement {
    List<XMLComponent> children;

    public XMLComposite(String tagName) {
        super(tagName);
        children = new ArrayList<>();
    }

    @Override
    public String print(String indent) {
        return String.format("%s<%s%s>\n%s\n%s</%s>",
                indent,
                tagName,
                attributes.entrySet().stream().map(entry -> String.format(" %s=\"%s\"", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(" ")), children.stream().map(child -> child.print(indent + "\t")).collect(Collectors.joining("\n")),
                indent,
                tagName);
    }

    public void addComponent(XMLComponent component) {
        children.add(component);
    }

}

