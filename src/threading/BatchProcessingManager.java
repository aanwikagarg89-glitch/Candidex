package threading;

import model.Application;
import model.PlacementDrive;
import model.Student;
import model.enums.ApplicationStatus;
import repository.ApplicationRepository;
import repository.DriveRepository;
import repository.StudentRepository;
import service.DriveService;

import java.util.List;

/**
 * Orchestrator for concurrent application batch processing.
 * Demonstrates Thread lifecycle (start, join), concurrency orchestration,
 * and the Producer-Consumer pattern with worker threads implementing Runnable.
 */
public class BatchProcessingManager {

    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private final DriveService driveService;

    public BatchProcessingManager(StudentRepository studentRepo, DriveRepository driveRepo,
                                  ApplicationRepository appRepo, DriveService driveService) {
        this.studentRepository = studentRepo;
        this.driveRepository = driveRepo;
        this.applicationRepository = appRepo;
        this.driveService = driveService;
    }

    /**
     * Executes multithreaded batch processing on a list of pending applications.
     */
    public void executeBatchProcessing(List<Application> pendingApplications) {
        if (pendingApplications == null || pendingApplications.isEmpty()) {
            System.out.println("No pending applications found in the system for batch verification.");
            return;
        }

        System.out.println("================================================================");
        System.out.println("         INITIALIZING MULTITHREADED BATCH PROCESSOR             ");
        System.out.println("================================================================");
        System.out.printf("Pending Applications to Process : %d%n", pendingApplications.size());
        System.out.println("Spawning 1 Producer Thread and 2 Parallel Consumer Threads...");

        long startTime = System.currentTimeMillis();

        ApplicationQueue queue = new ApplicationQueue(5);

        Producer producerTask = new Producer(queue, pendingApplications);
        Consumer consumerTask1 = new Consumer(queue, "Worker-Alpha",
                studentRepository, driveRepository, applicationRepository, driveService);
        Consumer consumerTask2 = new Consumer(queue, "Worker-Beta",
                studentRepository, driveRepository, applicationRepository, driveService);

        Thread producerThread = new Thread(producerTask, "Producer-Thread");
        Thread consumerThread1 = new Thread(consumerTask1, "Consumer-Thread-1");
        Thread consumerThread2 = new Thread(consumerTask2, "Consumer-Thread-2");

        // Start all threads
        producerThread.start();
        consumerThread1.start();
        consumerThread2.start();

        try {
            // Await completion of all threads using join()
            producerThread.join();
            consumerThread1.join();
            consumerThread2.join();
        } catch (InterruptedException e) {
            System.err.println("Batch processing manager was interrupted while waiting for worker threads.");
            Thread.currentThread().interrupt();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("================================================================");
        System.out.println("              BATCH PROCESSING PIPELINE FINISHED                ");
        System.out.println("================================================================");
        System.out.printf("All %d applications processed concurrently in %d ms.%n", pendingApplications.size(), elapsedTime);
        System.out.println("Application statuses have been updated in the in-memory repository.");
        System.out.println("================================================================");
    }

    /**
     * Worker thread simulating incoming application ingestion into the processing pipeline.
     */
    public static class Producer implements Runnable {
        private final ApplicationQueue queue;
        private final List<Application> batchList;

        public Producer(ApplicationQueue queue, List<Application> batchList) {
            this.queue = queue;
            this.batchList = batchList;
        }

        @Override
        public void run() {
            System.out.printf("  [Producer %s] Started batch ingestion of %d applications.%n",
                    Thread.currentThread().getName(), batchList.size());
            try {
                for (Application app : batchList) {
                    queue.enqueue(app);
                    System.out.printf("  [Producer %s] Enqueued application [%s] for student [%s].%n",
                            Thread.currentThread().getName(), app.getApplicationId(), app.getStudentRollNumber());
                    Thread.sleep(60); // Simulate ingestion network latency
                }
            } catch (InterruptedException e) {
                System.err.printf("  [Producer %s] Interrupted unexpectedly.%n", Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            } finally {
                queue.markProductionFinished();
                System.out.printf("  [Producer %s] Completed ingestion of all batch items.%n", Thread.currentThread().getName());
            }
        }
    }

    /**
     * Worker thread simulating background verification agents.
     */
    public static class Consumer implements Runnable {
        private final ApplicationQueue queue;
        private final String workerName;
        private final StudentRepository studentRepository;
        private final DriveRepository driveRepository;
        private final ApplicationRepository applicationRepository;
        private final DriveService driveService;

        public Consumer(ApplicationQueue queue, String workerName,
                        StudentRepository studentRepo, DriveRepository driveRepo,
                        ApplicationRepository appRepo, DriveService driveService) {
            this.queue = queue;
            this.workerName = workerName;
            this.studentRepository = studentRepo;
            this.driveRepository = driveRepo;
            this.applicationRepository = appRepo;
            this.driveService = driveService;
        }

        @Override
        public void run() {
            System.out.printf("  [Consumer %s] Worker online and awaiting tasks.%n", workerName);
            try {
                while (true) {
                    Application app = queue.dequeue();
                    if (app == null) {
                        break; // Queue drained and producer finished
                    }

                    // Verify credentials and eligibility
                    Student student = studentRepository.findById(app.getStudentId()).orElse(null);
                    PlacementDrive drive = driveRepository.findDriveById(app.getDriveId()).orElse(null);

                    if (student != null && drive != null) {
                        List<String> failures = driveService.getEligibilityDiagnosticReasons(student, drive);
                        if (failures.isEmpty()) {
                            app.updateStatus(ApplicationStatus.SHORTLISTED, "Automated Batch Verification Passed.");
                            System.out.printf("    ✔ [%s] Application [%s] for %s (%s) -> SHORTLISTED%n",
                                    workerName, app.getApplicationId(), student.getFullName(), student.getRollNumber());
                        } else {
                            app.updateStatus(ApplicationStatus.REJECTED, "Failed Batch Verification: " + failures.get(0));
                            System.out.printf("    ✘ [%s] Application [%s] for %s (%s) -> REJECTED (Criteria unmet)%n",
                                    workerName, app.getApplicationId(), student.getFullName(), student.getRollNumber());
                        }
                        applicationRepository.save(app);
                    }

                    Thread.sleep(80); // Simulate verification computation
                }
            } catch (InterruptedException e) {
                System.err.printf("  [Consumer %s] Interrupted unexpectedly.%n", workerName);
                Thread.currentThread().interrupt();
            } finally {
                System.out.printf("  [Consumer %s] Work completed. Shutting down worker.%n", workerName);
            }
        }
    }
}
