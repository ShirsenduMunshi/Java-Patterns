// import java.util.Scanner;
// public class Main {
//     // Reset
//     public static final String RESET = "\u001B[0m";  // Reset to default color

//     // Regular Colors
//     public static final String BLACK = "\u001B[30m";
//     public static final String RED = "\u001B[31m";
//     public static final String GREEN = "\u001B[32m";
//     public static final String YELLOW = "\u001B[33m";
//     public static final String BLUE = "\u001B[34m";
//     public static final String PURPLE = "\u001B[35m";
//     public static final String CYAN = "\u001B[36m";
//     public static final String WHITE = "\u001B[37m";

//      // Background Colors
//      public static final String BLACK_BG = "\u001B[40m";
//      public static final String RED_BG = "\u001B[41m";
//      public static final String GREEN_BG = "\u001B[42m";
//      public static final String YELLOW_BG = "\u001B[43m";
//      public static final String BLUE_BG = "\u001B[44m";
//      public static final String PURPLE_BG = "\u001B[45m";
//      public static final String CYAN_BG = "\u001B[46m";
//      public static final String WHITE_BG = "\u001B[47m";

//     public static void main(String[] args) {

//         Scanner sc = new Scanner(System.in);
//         System.out.print("Enter the number of lines: ");
//         int line = sc.nextInt();
//         int temp = 0;
//         sc.close();
//         System.out.print("\n");
//         temp = line;

//        for (int i=line;i>=0;i--){
//            for (int j=1;j<=i;j++){
//                System.out.print(" ");
//            }
//            for (int j=temp-1;j>i;j--){
//                 System.out.print(RED_BG + WHITE + "*" + RESET);
//            }
//            for (int j=temp;j>i;j--){
//                System.out.print(RED_BG + WHITE + "*" + RESET);
//            }
//            for (int j=1;j<=i;j++){
//                System.out.print(" ");
//            }
//            for (int j=1;j<=i;j++){
//                System.out.print(" ");
//            }
//            for (int j=temp;j>i;j--){
//                 System.out.print(RED_BG + WHITE + "*" + RESET);
//            }
//            for (int j=temp-1;j>i;j--){
//                System.out.print(RED_BG + WHITE + "*" + RESET);
//            }
//            System.out.print("\n");
//        }

//     for(int lp=0;lp<=2*line-1;lp++){
//         for(int j=0;j<lp;j++){
//             System.out.print(" ");
//         }
//         for(int j=line*2-2;j>=lp;j--){
//             System.out.print(RED_BG + WHITE + "*" + RESET);
//         }
//         for(int m=line*2-2;m>=lp;m--){
//             System.out.print(RED_BG + WHITE + "*" + RESET);
//         }
//         System.out.print("\n");
//     }
//     }

// }

import java.util.Scanner;

public class Heart {
    // Reset
    public static final String RESET = "\u001B[0m";  // Reset to default color
    // Regular Colors
    public static final String WHITE = "\u001B[37m";
    // Background Colors
    public static final String RED_BG = "\u001B[41m";
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of lines: ");
        int line = sc.nextInt();
        sc.close();
        System.out.print("\n");
        int temp = line;
        // Upper heart
        for (int i = line; i >= 0; i--) {
            for (int j = 1; j <= i; j++) System.out.print(" ");
            for (int j = temp - 1; j > i; j--) printStar();
            for (int j = temp; j > i; j--) printStar();
            for (int j = 1; j <= i; j++) System.out.print(" ");
            for (int j = 1; j <= i; j++) System.out.print(" ");
            for (int j = temp; j > i; j--) printStar();
            for (int j = temp - 1; j > i; j--) printStar();
            System.out.print("\n");
        }
        // Lower heart
        for (int lp = 0; lp <= 2 * line - 1; lp++) {
            for (int j = 0; j < lp; j++) System.out.print(" ");
            for (int j = line * 2 - 2; j >= lp; j--) printStar();
            for (int m = line * 2 - 2; m >= lp; m--) printStar();
            System.out.print("\n");
        }
    }
    // Function to print a star with delay
    private static void printStar() {
        try {
            System.out.print(RED_BG + WHITE + "*" + RESET);
            Thread.sleep(20); // Delay for typing effect
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}