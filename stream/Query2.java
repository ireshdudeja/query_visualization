import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import com.github.javafaker.Faker;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.Random;


/**
 * Implements a more or less useless query on some random data stream.
 * Generator -> Filter -> Select
 *
 * Libraries used:
 *
 * 1. JavaFaker: To generate random data
 * Source: https://github.com/DiUS/java-faker
 *
 * 2. HttpClient: To make GET Request
 * Source: https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
 *
 */

public class Query2 {
    public static void main(String[] args) throws Exception {

        // update URL
        Sender sender = new Sender("http://localhost:4000/modify");


        // the source is some data generator
        Stream.generate(() -> {

            // Generating Random data for person
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String streetAddress = faker.address().streetAddress();
            String phoneNumber = faker.phoneNumber().cellPhone();

            // Generating random stats
            Random rand = new Random();
            int rand_cpu = rand.nextInt(5000);
            int rand_io = rand.nextInt(100);
            int rand_operator = rand.nextInt(100);


            // update Stats to send later
            Stats.generatorNumTuples++;
            Stats.generatorCPUCost = rand_cpu;
            Stats.generatorIOCost = rand_io;
            Stats.generatorOperatorCost = rand_operator;

            // Creating person object
            Person tPerson = new Person(firstName, lastName, streetAddress, phoneNumber);

            return tPerson;
        })
                .limit(50000)
                .filter(p -> {
                    // Filter tuple
                    boolean result = p.firstName.contains("a");

                    Random rand = new Random();
                    int rand_cpu = rand.nextInt(50);
                    int rand_io = rand.nextInt(100);
                    int rand_operator = rand.nextInt(100);


                    // update Stats to send later
                    Stats.filterNumTuples++;
                    Stats.filterCPUCost = rand_cpu;
                    Stats.filterIOCost = rand_io;
                    Stats.filterOperatorCost = rand_operator;

                    return result; // true/false
                })
                .forEach(tPerson -> {
                  Random rand = new Random();
                  int rand_cpu = rand.nextInt(50);
                  int rand_io = rand.nextInt(100);
                  int rand_operator = rand.nextInt(100);


                  // update Stats to send later
                  Stats.selectNumTuples++;
                  Stats.selectCPUCost = rand_cpu;
                  Stats.selectIOCost = rand_io;
                  Stats.selectOperatorCost = rand_operator;


                  try {
                      System.out.println(tPerson.personId);
                      sender.send();
                      }
                      catch (Exception ex) {
                            System.out.println("Exception is: "+ ex.getMessage());
                            }
                        });

                // print to screen
                // .forEach(person -> System.out.println(person.personId + " " + person.firstName));

    }

}

class Person {
    private static final AtomicInteger count = new AtomicInteger(0);
    final int personId;
    String firstName;
    String lastName;
    String phoneNumber;
    String streetAddress;

    public Person(String fName, String lName, String sAddress, String pNumber){
        personId = count.incrementAndGet();
        firstName = fName;
        lastName = lName;
        streetAddress = sAddress;
        phoneNumber = pNumber;
    }

}


/**
 * Used to collect our statistics
 */
class Stats {
    static long generatorNumTuples = 0;
    static int generatorCPUCost = 0;
    static int generatorIOCost = 0;
    static int generatorOperatorCost = 0;

    static int filterNumTuples = 0;
    static int filterCPUCost = 0;
    static int filterIOCost = 0;
    static int filterOperatorCost = 0;

    static int selectNumTuples = 0;
    static long selectCPUCost = 0;
    static long selectIOCost = 0;
    static int selectOperatorCost = 0;

}


class Sender {
    final URL baseUrl;
    public Sender(String baseUrl) throws Exception {
        this.baseUrl = new URL(baseUrl);
    }

    public void send() throws Exception {

        // Sending data for one operator
        String url = this.baseUrl +
                    "?id=1" +
                    "&num_tuples="+ String.valueOf(Stats.generatorNumTuples) +
                    "&operator_cost="+String.valueOf(Stats.generatorOperatorCost) +
                    "&io_cost="+String.valueOf(Stats.generatorIOCost) +
                    "&cpu_cost=" + String.valueOf(Stats.generatorCPUCost);


        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }

    }
}
