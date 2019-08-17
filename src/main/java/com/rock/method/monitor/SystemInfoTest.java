package com.rock.method.monitor;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static oshi.hardware.CentralProcessor.*;
import static oshi.software.os.OperatingSystem.*;

public class SystemInfoTest {

    /**
     * The main method, demonstrating use of classes.
     *
     * @param args
     *            the arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("初始化系统：Initializing System...");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

//        //------------------------------
//        CentralProcessor processor = hal.getProcessor();
//        for (int i = 0; i < 30; i++) {
//            long[] prevTicks = processor.getSystemCpuLoadTicks();
//            System.out.println(String.format("CPU load: %.1f%%", hal.getProcessor().getSystemCpuLoadBetweenTicks(prevTicks) * 100));
//            try{
//                Thread.sleep(1 * 1000);
//            }catch(Exception e){}
//        }

        printOperatingSystem(os);

        System.out.println("检测系统：Checking computer system...");
        printComputerSystem(hal.getComputerSystem());

        System.out.println("检测处理器：Checking Processor...");
        printProcessor(hal.getProcessor());

        System.out.println("检测内存：Checking Memory...");
        printMemory(hal.getMemory());

        System.out.println("检测CPU：Checking CPU...");
        printCpu(hal.getProcessor());

        System.out.println("检测进程：Checking Processes...");
        printProcesses(os, hal.getMemory());

        System.out.println("检测传感器：Checking Sensors...");
        printSensors(hal.getSensors());

        System.out.println("检测电源：Checking Power sources...");
        printPowerSources(hal.getPowerSources());

        System.out.println("检测硬盘：Checking Disks...");
        printDisks(hal.getDiskStores());

        System.out.println("检测文件系统：Checking File System...");
        printFileSystem(os.getFileSystem());

        System.out.println("检测网络接口：Checking Network interfaces...");
        printNetworkInterfaces(hal.getNetworkIFs());

        System.out.println("检测网络参数：Checking Network parameters...");
        printNetworkParameters(os.getNetworkParams());

        // hardware: displays
        System.out.println("检测硬盘并打印：Checking Displays...");
        printDisplays(hal.getDisplays());

        // hardware: USB devices
        System.out.println("检测USB设备：Checking USB Devices...");
        printUsbDevices(hal.getUsbDevices(true));

        System.out.println("检测声卡：Checking Sound Cards...");
        printSoundCards(hal.getSoundCards());

    }

    private static void printOperatingSystem(final OperatingSystem os) {
        System.out.println(String.valueOf(os));
        System.out.println("Booted: " + Instant.ofEpochSecond(os.getSystemBootTime()));
        System.out.println("Uptime: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
        System.out.println("Running with" + (os.isElevated() ? "" : "out") + " elevated permissions.");
    }

    private static void printComputerSystem(final ComputerSystem computerSystem) {

        System.out.println(computerSystem.toString());
        final Firmware firmware = computerSystem.getFirmware();
        System.out.println("firmware: " + firmware.toString());
        final Baseboard baseboard = computerSystem.getBaseboard();
        System.out.println("baseboard: " + baseboard.toString());
    }

    private static void printProcessor(CentralProcessor processor) {
        System.out.println(processor.toString());
    }

    private static void printMemory(GlobalMemory memory) {
        System.out.println("Memory: \n " + (memory.getAvailable()/1024/1024/1024) + "GB/" + (memory.getTotal()/1024/1024/1024) + "GB");
        VirtualMemory vm = memory.getVirtualMemory();
        System.out.println("Swap: \n " + (vm.getSwapUsed()/1024/1024) + "MB/" + (vm.getSwapTotal()/1024/1024) + "MB");
    }

    private static void printCpu(CentralProcessor processor) {
        System.out.println("Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        System.out.println("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        System.out.println("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        System.out.println(
                String.format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%",
                100d * user / totalCpu,
                100d * nice / totalCpu,
                100d * sys / totalCpu,
                100d * idle / totalCpu,
                100d * iowait / totalCpu,
                100d * irq / totalCpu,
                100d * softirq / totalCpu,
                100d * steal / totalCpu
                )
        );
        System.out.println(String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
        double[] loadAverage = processor.getSystemLoadAverage(3);
        System.out.println("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        System.out.println(procCpu.toString());
        long freq = processor.getVendorFreq();
        if (freq > 0) {
            System.out.println("Vendor Frequency: " + FormatUtil.formatHertz(freq));
        }
        freq = processor.getMaxFreq();
        if (freq > 0) {
            System.out.println("Max Frequency: " + FormatUtil.formatHertz(freq));
        }
        long[] freqs = processor.getCurrentFreq();
        if (freqs[0] > 0) {
            StringBuilder sb = new StringBuilder("Current Frequencies: ");
            for (int i = 0; i < freqs.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(FormatUtil.formatHertz(freqs[i]));
            }
            System.out.println(sb.toString());
        }
    }

    private static void printProcesses(OperatingSystem os, GlobalMemory memory) {
        System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<OSProcess> procs = Arrays.asList(os.getProcesses(5, ProcessSort.CPU));

        System.out.println("   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size() && i < 5; i++) {
            OSProcess p = procs.get(i);
            System.out.println(String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
        }
    }

    private static void printSensors(Sensors sensors) {
        System.out.println("Sensors:");
        System.out.println(String.format(" CPU Temperature: %.1f°C%n", sensors.getCpuTemperature()));
        System.out.println(" Fan Speeds: " + Arrays.toString(sensors.getFanSpeeds()));
        System.out.println(String.format(" CPU Voltage: %.1fV%n", sensors.getCpuVoltage()));
    }

    private static void printPowerSources(PowerSource[] powerSources) {
        StringBuilder sb = new StringBuilder("Power: ");
        if (powerSources.length == 0) {
            sb.append("Unknown");
        }
        for (PowerSource powerSource : powerSources) {
            sb.append("\n ").append(powerSource.toString());
        }
        System.out.println(sb.toString());
    }

    private static void printDisks(HWDiskStore[] diskStores) {
        System.out.println("Disks:");
        for (HWDiskStore disk : diskStores) {
            System.out.println(" " + disk.toString());

            HWPartition[] partitions = disk.getPartitions();
            for (HWPartition part : partitions) {
                System.out.println(" |-- " + part.toString());
            }
        }

    }

    private static void printFileSystem(FileSystem fileSystem) {
        System.out.println("File System:");

        System.out.println(String.format(" File Descriptors: %d/%d", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors()));

        OSFileStore[] fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            System.out.println(String.format(
                    " %s (%s) [%s] %s of %s free (%.1f%%), %s of %s files free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    FormatUtil.formatValue(fs.getFreeInodes(), ""), FormatUtil.formatValue(fs.getTotalInodes(), ""),
                    100d * fs.getFreeInodes() / fs.getTotalInodes(), fs.getVolume(), fs.getLogicalVolume(),
                    fs.getMount()));
        }
    }

    private static void printNetworkInterfaces(NetworkIF[] networkIFs) {
        System.out.println("Network interfaces:");
        for (NetworkIF net : networkIFs) {
            System.out.println(String.format(" Name: %s (%s)%n", net.getName(), net.getDisplayName()));
            System.out.println(String.format("   MAC Address: %s %n", net.getMacaddr()));
            System.out.println(String.format("   MTU: %s, Speed: %s %n", net.getMTU(),
                    FormatUtil.formatValue(net.getSpeed(), "bps")));
            System.out.println(String.format("   IPv4: %s %n", Arrays.toString(net.getIPv4addr())));
            System.out.println(String.format("   IPv6: %s %n", Arrays.toString(net.getIPv6addr())));
            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
                    || net.getPacketsSent() > 0;
            System.out.println(String.format("   Traffic: received %s/%s%s; transmitted %s/%s%s",
                    hasData ? net.getPacketsRecv() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
                    hasData ? " (" + net.getInErrors() + " err)" : "",
                    hasData ? net.getPacketsSent() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
                    hasData ? " (" + net.getOutErrors() + " err)" : ""));
        }
    }

    private static void printNetworkParameters(NetworkParams networkParams) {
        System.out.println("Network parameters:");
        System.out.println(String.format(" Host name: %s%n", networkParams.getHostName()));
        System.out.println(String.format(" Domain name: %s%n", networkParams.getDomainName()));
        System.out.println(String.format(" DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers())));
        System.out.println(String.format(" IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway()));
        System.out.println(String.format(" IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway()));
    }

    private static void printDisplays(Display[] displays) {
        System.out.println("Displays:");
        int i = 0;
        for (Display display : displays) {
            System.out.println(" Display " + i + ":");
            System.out.println(String.valueOf(display));
            i++;
        }
    }

    private static void printUsbDevices(UsbDevice[] usbDevices) {
        System.out.println("USB Devices:");
        for (UsbDevice usbDevice : usbDevices) {
            System.out.println(String.valueOf(usbDevice));
        }
    }

    private static void printSoundCards(SoundCard[] cards) {
        System.out.println("Sound Cards:");
        for (SoundCard card : cards) {
            System.out.println(String.valueOf(card));
        }
    }
}
