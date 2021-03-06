import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

public class Main1 {
    FileReader fileReader;
    public HashMap<Integer,Integer>utility = new HashMap<>();
    public HashMap<Integer,ArrayList<Integer>>transactionUtility = new HashMap<>();
    public HashMap<Integer,TransactionUtility1> TWU = new HashMap<>();
    public HashMap<Integer,ArrayList<Integer>>transaction = new HashMap<>();
    public HashMap<Integer,ArrayList<Integer>>ntransaction = new HashMap<>();
    public ArrayList<Integer> primarySorting = new ArrayList<>();
    public HashMap<Integer,Integer> subtree = new HashMap<>();
    ArrayList<TransactionUtility1> list=new ArrayList<TransactionUtility1>();
    Comparator<TransactionUtility1>comparator = new Comparator<TransactionUtility1>() {
        @Override
        public int compare(TransactionUtility1 o1, TransactionUtility1 o2) {
            if(o1.getTwu()<o2.getTwu()){
                return -1;
            }
            if(o1.getTwu()>o2.getTwu()){
                return 1;
            }
            return 0;
        }
    };
    public int minUtility;
    public int numTrans;

    public void readFile(String filename)throws Exception{
        fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String temp;
        int i=0;
        ArrayList<Integer> trans;
        while((temp=bufferedReader.readLine())!=null){
            //System.out.println(temp);
            String line[]=temp.split(":");

            trans = new ArrayList<>();
            for (String x:line[0].split("\\s+")){
                trans.add(Integer.parseInt(x));
            }
            transaction.put(i,trans);
            trans = new ArrayList<>();
            for (String x:line[2].split("\\s+")){
                trans.add(Integer.parseInt(x));
            }

            transactionUtility.put(i,trans);
            utility.put(i,Integer.parseInt(line[1]));
            i++;
        }
        numTrans = i;
        //Scanner scanner = new Scanner(System.in);
        //System.out.println("enter min utility");
        this.minUtility = 20000;

    }


    public ArrayList<TransactionUtility1> sort(ArrayList<TransactionUtility1> trans)
    {
        int n = trans.size();
        int min_idx;

        for (int i = 0; i < n-1; i++)
        {
            // Find the minimum element in unsorted array
            min_idx = i;
            for (int j = i+1; j < n; j++)
                if (trans.get(j).getTwu()<trans.get(min_idx).getTwu())
                    min_idx = j;

            // Swap the found minimum element with the first element
            TransactionUtility1 t = trans.get(i);
            trans.set(i,trans.get(min_idx));
            trans.set(min_idx,t);

        }

        /*
        for(int i=0;i<n-1;i++){
            for(int j=0;j<n-i-1;j++){
                if(trans.get(j).getTwu()>trans.get(j+1).getTwu()){
                    TransactionUtility1 t = trans.get(j);
                    trans.set(j,trans.get(j+1));
                    trans.set(j+1,t);
                }
            }
        }*/
        return trans;
    }

    public void findTWU(){
        ArrayList<Integer> transNo;
        for(int i=0;i<numTrans;i++){
            transNo = transaction.get(i);
            for (int x:transNo){
                if(TWU.containsKey(x)){
                    TransactionUtility1 t = TWU.get(x);
                    t.updateTWU(utility.get(i));
                    TWU.put(x,t);
                }else{
                    TransactionUtility1 t = new TransactionUtility1(x,utility.get(i));
                    TWU.put(x,t);
                }
            }
        }
        

        for(TransactionUtility1 t:TWU.values()){

            list.add(t);
        }

    }

    public void getPrimarySorting(){
        list = sort(list);

        for(TransactionUtility1 t:list){
            if(t.getTwu()>=minUtility){
                primarySorting.add(t.getTransaction());
                System.out.println(t.getTransaction()+" -> "+t.getTwu());
            }
        }
        //System.out.println("out of loop");
        System.out.println("Primary sorting "+primarySorting.toString());
    }



    public void orderTrans(){
        for(int i=0;i<transaction.size();i++){
            ArrayList<Integer> x = transaction.get(i);
            ArrayList<Integer> newX = new ArrayList<>();
            for(int y:primarySorting){
                if(x.contains(y)){
                    newX.add(y);
                }
            }
            //System.out.println(i+" -> "+newX.toString());
            ntransaction.put(i,newX);
        }
    }

    public void buildSubTree(){
        int su=0,t=0;
        for(int i=0;i<primarySorting.size();i++) {
            su = 0;
            int x = primarySorting.get(i);
            for (int j = 0; j < ntransaction.size(); j++) {
                t = j;
                ArrayList<Integer> y = ntransaction.get(j);
                for (int z = 0; z <= x; z++) {
                    if (y.contains(z) && primarySorting.contains(z)) {
                        su = su + transactionUtility.get(j).get(transaction.get(j).indexOf(z));
                    }
                }
            }
            if (su >= minUtility) {
                subtree.put(x, su);
                //System.out.println(transaction.get(x).toString()+" -> "+subtree.get(x));
            }
        }
        int k=0;
        for(int x:subtree.keySet()){
            System.out.println(x+" -> "+subtree.get(x));
            k++;
        }
    }

    public void printHighUtility(){
        System.out.println("High Utility : ");
        for(int k=0;k<subtree.size();k++){
            System.out.println(ntransaction.get(k).toString());
        }

    }


    public static void main(String[] args) throws Exception {
        Main1 main = new Main1();
        long start = System.currentTimeMillis();
        main.readFile("/home/karthik/Desktop/Untitled Folder/chess.txt");
        main.findTWU();
        main.getPrimarySorting();
        main.orderTrans();
        main.buildSubTree();
        main.printHighUtility();
        long end = System.currentTimeMillis();
        System.out.println("Time : "+(end-start)+" ms");
    }


}

class TransactionUtility1{
    int transaction;
    int twu;

    public TransactionUtility1(int transaction,int twu){
        this.transaction=transaction;
        this.twu=twu;
    }
    public void updateTWU(int value){
        this.twu = this.twu+value;
    }

    public int getTransaction() {
        return transaction;
    }

    public int getTwu() {
        return twu;
    }
}
