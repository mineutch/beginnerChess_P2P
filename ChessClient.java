import java.io.*;
import java.net.*;
import java.util.Scanner;



public class ChessClient {
    static String red    = "\u001b[31m";
    static String redback   = "\u001b[41m";
    static String whiteback   = "\u001b[47m";
    static String blackback   = "\u001b[40m";
    static String whitebackred  = "\u001b[00;47;31m";
    static String whitebackblack  = "\u001b[00;47;30m";
    static String bluebackwhite  = "\u001b[00;46;37m";
    static String redbackwhite  = "\u001b[00;41;37m";
    static String bold  = "\u001b[1m";
    static String white  = "\u001b[37m";
    static String green  = "\u001b[32m";
    static String yellow = "\u001b[00;33m";
    static String blue = "\u001b[36m";  
    static String end    = "\u001b[00m";
    static String endback    = "\u001b[49m";
    static char[][] chess = {
            {'R','e'},{'N','e'},{'B','e'},{'K','e'},{'Q','e'},{'B','e'},{'N','e'},{'R','e'},
            {'P','e'},{'P','e'},{'P','e'},{'P','e'},{'P','e'},{'P','e'},{'P','e'},{'P','e'},
            {'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},
            {'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},
            {'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},
            {'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},{'0','0'},
            {'P','m'},{'P','m'},{'P','m'},{'P','m'},{'P','m'},{'P','m'},{'P','m'},{'P','m'},
            {'R','m'},{'N','m'},{'B','m'},{'K','m'},{'Q','m'},{'B','m'},{'N','m'},{'R','m'}
    };
    static int PORT = 8080;
    static int win =0;
    static int rightCastling =1;//キャスリングができる->1
    static int leftCastling =1;
    static int erightCastling =1;
    static int eleftCastling =1;

    public static void main(String[] args) throws IOException{
        InetAddress addr = InetAddress.getByName("localhost");//IPアドレス変換
        System.out.println("addr ="+addr);
        
  
        if(args.length==1){
            PORT=Integer.parseInt(args[0]);
        }

        Socket socket = new Socket(addr,PORT);//ソケット作成

        try{
            System.out.println("socket = "+socket);
            BufferedReader in =new BufferedReader( new InputStreamReader(socket.getInputStream()));//Data受信用バッファ
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);//送信用バッファ
            
            //ここから処理をかく
            kakunin1();
            out.println("OK");//cilentから送る
            String answer = in.readLine();
            answer.equals("OK");//ゲームを始めるぜ
            display();
            int flag=0;
            int turn=1;

            while(flag==0){
                check(1);
                int before=movefrom();//clientが先制
                int after=move(before);
                out.println(before);
                answer = in.readLine();
                answer.equals("OK");//ゲームを始めるぜ
                out.println(after);
                if(win==1){
                    System.out.println(redbackwhite+" - 自分のターン - "+end+endback+"       You win     ");
                    break;
                }            
                check(2);
                System.out.println(whitebackblack+" - 相手のターン - "+end+endback+"   wait for answer "+"\n");
                answer = in.readLine(); 
                before = 63-Integer.parseInt(answer);
                out.println("OK");//行けるぜ
                answer = in.readLine(); 
                after = 63-Integer.parseInt(answer);
                moveenemy(before,after);
                if(win==-1){
                    System.out.println(redbackwhite+" - 自分のターン - "+end+endback+"       You lose     ");
                    break;
                }                 
                turn++;
                if(turn>30)flag=1;
            }

            out.println("END");

        }finally{
            System.out.println("closing...");
            socket.close();
        }
    }

    static void kakunin1(){
        while(true){
            System.out.println("\n"+redbackwhite+"          - Original Chess Game -         / １ ▶︎ start  "+end+endback);
            Scanner sc = new Scanner(System.in);
            int s= sc.nextInt();
            if (s==1)break;
        }
        System.out.println(whitebackblack+" -  game start  - "+end+endback+"   wait for answer "+"\n");
    }

    static int movefrom(){
        int x,y;
        while(true){
                System.out.println(redbackwhite+" - 自分のターン - "+end+endback+"   [ 座標を入力 ]  ");
                Scanner sc = new Scanner(System.in);
                x= sc.nextInt();
                y= sc.nextInt();
                rule(chess[(x-1)+((8-y)%8)*8][0]);
                if(chess[(x-1)+((8-y)%8)*8][1]=='m'&&chess[(x-1)+((8-y)%8)*8][0]!='0')break;

            }
        return (x-1)+((8-y)%8)*8;
    }

    static int move(int x){
        int z,w;
            while(true){
                System.out.println(redbackwhite+" - 自分のターン - "+end+endback+" "+chess[x][0]+" [移動座標を入力]");
                Scanner sc = new Scanner(System.in);
                z = sc.nextInt();
                w = sc.nextInt();
                if(chess[(z-1)+((8-w)%8)*8][1]!='m')break;
            }
            if(chess[(z-1)+((8-w)%8)*8][0]=='K'){
                win=1;//勝利フラグ
            }
            if(chess[x][0]=='P'&&x-16==(z-1)+((8-w)%8)*8){//2マス移動
                chess[x-8][1]='1';
            }//2マス移動                    
            if(chess[x][0]=='R'){
                if(x==63){
                    rightCastling=0;
                }
                if(x==56){
                    leftCastling=0;
                }
            }//キャスリング不可能
            if(chess[x][0]=='K'){
                if((z-1)+((8-w)%8)*8==61&&rightCastling==1){
                    //K,Rどちらも動いてない
                    rightCastling=0;
                    if(chess[61][0]=='0'&&chess[60][0]=='0'&&chess[62][0]=='0'){//KのRの間にコマがない
                        //Rのみ動かす
                        chess[60][0]='R';
                        chess[60][1]='m';
                        chess[63][0]='0';
                        chess[63][1]='0';
                        System.out.println(redbackwhite+" - 自分のターン - "+end+endback+"   <- Castling ->  ");    
                    }
                }
                else if((z-1)+((8-w)%8)*8==57&&leftCastling==1){
                    leftCastling=0;
                     if(chess[58][0]=='0'&&chess[57][0]=='0'){
                        //Rのみ動かす
                        chess[58][0]='R';
                        chess[58][1]='m';
                        chess[56][0]='0';
                        chess[56][1]='0';
                        System.out.println(redbackwhite+" - 自分のターン - "+end+endback+"   <- Castling ->  ");                            
                    }
                }
                rightCastling=0;
                leftCastling=0;
            }//キャスリング不可能            

            if(chess[(z-1)+((8-w)%8)*8][1]=='2'){
                chess[(z-1)+((8-w)%8)*8+8][0]='0';
                chess[(z-1)+((8-w)%8)*8+8][1]='0';
                chess[(z-1)+((8-w)%8)*8][1]='0';
                System.out.println(redbackwhite+" - 自分のターン - "+end+endback+"   <- en passant ->  ");                            
            }

            chess[(z-1)+((8-w)%8)*8][1]=chess[x][1];
            chess[(z-1)+((8-w)%8)*8][0]=chess[x][0];
            chess[x][1]='0';
            chess[x][0]='0';
            display();
            return (z-1)+((8-w)%8)*8;
    }

    static void rule(char s){
        System.out.println("----------------------------------------------------------------");
        switch (s){
            case 'P':
                System.out.println("・pawn  基本的に前へ1マス進む。敵を倒すのは斜め前方への移動のみ。");
                System.out.println("      　※それぞれのPは、初期位置からの移動のみ2マス動ける。");
                System.out.println("       　また2マス移動の直後1ターンは1マス後方に残像を残し、");
                System.out.println("       　この残像が攻撃されると本体も消えてしまう。");
                System.out.println("      　※Pが最奥へ到達するとR,N,B,Qのうち好きなコマへ昇格できる。");
                break;
            case 'R':
                System.out.println("・rook  左右、上下好きな距離移動できる。(将棋における飛車)");
                break;
            case 'N':
                System.out.println("・knight  左右、上下好きな方に2マス進み、その左右に移動できる。");
                System.out.println("      　  (将棋における桂馬の前後左右バージョン)");
                break;            
            case 'B':
                System.out.println("・bishop  斜めで好きな距離移動できる。(将棋における角)");
                break;
            case 'Q':
                System.out.println("・queen  左右、上下、斜め好きな距離移動できる。BとRの動きを持つ");
                System.out.println("      　 (将棋における飛車と角)");
                break;
            case 'K':
                System.out.println("・king  左右、上下、斜め方向に1マス移動できる。");
                break;
        }
        System.out.println("----------------------------------------------------------------");
    }

    static void moveenemy(int before,int after){
        if(chess[after][0]=='K')win=-1;//負け
            if(chess[before][0]=='R'){
                if(before==7){
                    erightCastling=0;
                }
                if(before==0){
                    eleftCastling=0;
                }
            }//キャスリング不可能
            if(chess[before][0]=='P'&&before+16==after){
                chess[after-8][1]='2';
            }//Pが2移動             
            if(chess[before][0]=='K'){
                if(after==5&&erightCastling==1){
                    //K,Rどちらも動いてない
                    erightCastling=0;
                    if(chess[4][0]=='0'&&chess[5][0]=='0'&&chess[6][0]=='0'){//KのRの間にコマがない
                        //Rのみ動かす
                        chess[4][0]='R';
                        chess[4][1]='e';
                        chess[7][0]='0';
                        chess[7][1]='0';
                        System.out.println(whitebackblack+" - 相手のターン - "+end+endback+"   <- Castling ->  ");    
                    }
                }
                else if(after==1&&eleftCastling==1){
                    eleftCastling=0;
                     if(chess[1][0]=='0'&&chess[2][0]=='0'){
                        //Rのみ動かす
                        chess[2][0]='R';
                        chess[2][1]='e';
                        chess[0][0]='0';
                        chess[0][1]='0';
                        System.out.println(whitebackblack+" - 相手のターン - "+end+endback+"   <- Castling ->  ");                            
                    }
                }
                erightCastling=0;
                eleftCastling=0;
            }//キャスリング不可能            
            if(chess[after][1]=='1'){
                chess[after-8][0]='0';
                chess[after-8][1]='0';
                chess[after][1]='0';
                System.out.println(whitebackblack+" - 相手のターン - "+end+endback+"   <- en passant ->  ");                            
            }

        chess[after][1]=chess[before][1];
        chess[after][0]=chess[before][0];
        chess[before][1]='0';
        chess[before][0]='0';
        display();
        System.out.println(whitebackblack+" - 相手のターン - "+end+endback+"   "+chess[after][0]+"を動かしてきた "+"\n");
    }
    static void check(int a){
        for(int k=0;k<64;k++)
            if(chess[k][1]==a){
                chess[k][1]=0;
        }
    }

    static void display(){
        int j=1;
            for(int i=0;i<chess.length;i++){
                if(i%8==0){
                    System.out.println(end);
                    System.out.print(" "+(8-i/8)+" ");
                    j=-1*j;
                }
                if(j==1)System.out.print(whiteback);
                else System.out.print(blackback);
                j=-1*j;

                if(chess[i][1]=='e')System.out.print(blue);//敵のコマ
                else if (chess[i][1]=='m')System.out.print(red);//自分のコマ
                if(chess[i][0]=='0'){
                    System.out.print("   ");
                }else
                System.out.print(bold+" "+chess[i][0]+" ");
            }//表示
            System.out.println(end);
            System.out.println("    1  2  3  4  5  6  7  8 ");
            System.out.println();
    }

}