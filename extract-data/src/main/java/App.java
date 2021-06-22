import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.Statement;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class App {

    public static void main(String[] args) {
        int numberOfMethods = 0;
        int numberOfLoggedMethods = 0;
        int numberOfNotLoggedMethods = 0;

        String source = args[0];
        String target = args[1];
        CompilationUnit cu;
        File dir = new File(source);
        File[] directoryListing = dir.listFiles();
        JSONObject jo = new JSONObject();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                try {
                    cu = getCompilationUnit(file.getPath());
                    for (TypeDeclaration typeDec : cu.getTypes()) {
                        List<BodyDeclaration> members = typeDec.getMembers();
                        if (members != null) {
                            for (BodyDeclaration member : members) {
                                if (member.isMethodDeclaration()) {
                                    MethodDeclaration field = (MethodDeclaration) member;

                                    // Visit the method calls inside the method and
                                    // check if they correspond to log statements
                                    MethodCallVisitor visitor = new MethodCallVisitor();
                                    List<Boolean> isLoggedList = field.accept(visitor, null);
                                    boolean isLogged = isLoggedList.contains(true);



                                    // get all of the method's statements
                                    NodeList<Statement> statements = new NodeList<>();
                                    Object[] words = {};
                                    if(field.getBody().isPresent()) {
                                        statements = field.getBody().get().getStatements();
                                        statements = removeLogStatements(statements);

                                        // check if the method is left without words
                                        words = statements.stream()
                                                .map(x -> getWordsFromStatement(x))
                                                .filter(x -> x.size() > 0)
                                                .flatMap(List::stream).toArray();
                                    }

                                    if (words.length == 0) {
                                        continue;
                                    }
                                    numberOfMethods++;
                                    if (isLogged) {
                                        numberOfLoggedMethods++;
                                    } else {
                                        numberOfNotLoggedMethods++;
                                    }

                                    JSONObject method = new JSONObject();
                                    method.put("name", field.getNameAsString());
                                    if (! (statements == null) && field.getBody().isPresent()) {
                                        method.put("statements", field.getBody().get().getStatements().stream().map(x -> x.toString()).toArray());
                                    } else {
                                        String[] emptyArray = {};
                                        method.put("statements", emptyArray);
                                    }
                                    method.put("logged", isLogged);
                                    removeComments(field);
                                    method.put("LOC", getLinesOfCode(field));
                                    method.put("words", words);
                                    jo.put(Integer.toString(numberOfMethods), method);
                                    if(field.getBody().isPresent()) {
                                        method.put("body", field.getBody().get().toString());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(source + " is empty.");
        }

        System.out.println("Number of methods : " + numberOfMethods);
        System.out.println("  Logged: " + numberOfLoggedMethods);
        System.out.println("  Not Logged: " + numberOfNotLoggedMethods);

        // create JSON file
        FileWriter file = null;
        try {
            file = new FileWriter(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.write(jo.toString(2));
            System.out.println("Successfully created json file.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static CompilationUnit getCompilationUnit(String fileName) throws IOException {
        InputStream in = null;
        CompilationUnit cu = null;
        try
        {
            in = new FileInputStream (fileName);
            cu = StaticJavaParser.parse(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            in.close();
        }
        return cu;
    }

    public static int getLinesOfCode(MethodDeclaration method) {
        if (method.getBody().isPresent()) {
            return method.getBody().get().toString().split("\n").length;
        } else {
            return 0;
        }
    }

    static void removeComments(Node node) {
        for (Comment child : node.getAllContainedComments()) {
            child.remove();
        }
    }

    static List<String> getWordsFromStatement(Statement statement) {
        String s = statement.toString();
        List<String> temp =  Arrays.asList(s.split("[^A-Za-z0-9]+"));
        List<String> result = new ArrayList<>();
        for (String w: temp) {
            if (w.length() > 0) {
                char c = w.charAt(0);
                if (! Character.isDigit(c)) {
                    result.add(w);
                }
            }

        }
        return result;
    }

    static NodeList<Statement> removeLogStatements(NodeList<Statement> statements) {
        NodeList<Statement> result = new NodeList<>();
        if (statements.size() > 0) {
            for (Statement statement: statements) {
                if (! isLogStatement(statement.toString())) {
                    result.add(statement);
                }
            }
        }

        return result;
    }

    static boolean isLogStatement(String line) {
        line = line.toLowerCase().trim();

        String words[] = line.split("[^A-Za-z0-9]+");

        Pattern p = Pattern.compile("info|warn|debug|error|fatal|trace");
        for (String word: words) {
            if (p.matcher(word).find()) {
                return true;
            }
        }
        return false;
    }
}
