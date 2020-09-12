package pl.kamilkoszykowski.dopewriter;

import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class Controller {

    Set<String> dictionary = getDictionary("PL");

    @GetMapping("/dictionary/{language}")
    public String changeDictionary(@PathVariable String language) {
        dictionary = getDictionary(language);
        return language;
    }

    @GetMapping("/rhyme/{matchNumber}/{word}")
    public List<Dictionary> rhymes(@PathVariable String word, @PathVariable int matchNumber) throws InterruptedException {

        int wordLength = word.length();

        Set<String> rhymesSet = new HashSet<>();

        List<Dictionary> rhymesList = new ArrayList<>();

        int numRunnables = 128;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(numRunnables, true);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ExecutorService executor1 = new ThreadPoolExecutor(numRunnables, numRunnables, 0L, TimeUnit.MILLISECONDS, queue, handler);

        executor1.execute(new Runnable() {

            @Override
            public void run() {
                if (matchNumber == 5 && wordLength >= 5) {
                    for (String a : dictionary) {
                        if (a.endsWith(word.substring(wordLength - 5))) {
                            rhymesSet.add(a);
                        }
                    }

                } else if (matchNumber == 4 && wordLength >= 4) {
                    for (String a : dictionary) {
                        if (a.endsWith(word.substring(wordLength - 4))) {
                            rhymesSet.add(a);
                        }
                    }

                    if (wordLength >= 5) {
                        rhymesSet.removeIf(a -> a.endsWith(word.substring(wordLength - 5)));
                    }


                } else if (matchNumber == 3 && wordLength >= 3) {
                    for (String a : dictionary) {
                        if (a.endsWith(word.substring(wordLength - 3))) {
                            rhymesSet.add(a);
                        }
                    }

                    if (wordLength >= 4) {
                        rhymesSet.removeIf(a -> a.endsWith(word.substring(wordLength - 4)));
                    }

                    if (wordLength >= 5) {
                        rhymesSet.removeIf(a -> a.endsWith(word.substring(wordLength - 5)));
                    }

                } else if (matchNumber == 2 && wordLength == 2) {
                    for (String a : dictionary) {
                        if (a.endsWith(word)) {
                            rhymesSet.add(a);
                        }
                    }

                }

                for (String s : rhymesSet) {
                    Dictionary d = new Dictionary(s);
                    rhymesList.add(d);
                }

            }
        });

        executor1.shutdown();
        while (executor1.isTerminated() == false){
            Thread.sleep(50);
        }

        return rhymesList;
    }

    public Set<String> getDictionary(String language) {
        try {
            List<String> list = new ArrayList<>(List.of(Files.readString(Paths.get("src/main/resources/dictionary_" + language + ".txt")).split(",")));
            return new HashSet<>(list);
        } catch (IOException e) {
            return null;
        }
    }
}
