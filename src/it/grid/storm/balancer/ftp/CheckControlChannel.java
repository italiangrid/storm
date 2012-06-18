package it.grid.storm.balancer.ftp;


import java.net.InetSocketAddress;

public class CheckControlChannel
{
    public static boolean checkGFtpServer(InetSocketAddress address) throws Exception
    {
        return TelnetClient.check(address);
    }
}
