package com.light.challenge;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.enums.Department;
import com.light.challenge.service.WorkflowService;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApprovalWorkflowApplication implements CommandLineRunner {

  private final WorkflowService workflowService;

  public ApprovalWorkflowApplication(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  public static void main(String[] args) {
    SpringApplication.run(ApprovalWorkflowApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    if (args.length < 2) {
      printUsage();
      return;
    }

    try {
      var invoice = parseArguments(args);
      System.out.println("\n=== Processing Invoice ===");
      System.out.println("Amount: " + invoice.amount());
      System.out.println("Department: " + invoice.department());
      System.out.println("Requires Manager Approval: " + invoice.requiresManagerApproval());
      System.out.println();

      var result = workflowService.processInvoice(invoice);

      System.out.println("\n=== Result ===");
      if (result.success()) {
        System.out.println("Status: SUCCESS");
        System.out.println(
            "Approver: " + result.approverName() + " (" + result.approverType() + ")");
        System.out.println("Channel: " + result.channel());
      } else {
        System.out.println("Status: FAILED");
        System.out.println("Message: " + result.message());
      }
    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
      printUsage();
    }
  }

  private InvoiceRequest parseArguments(String[] args) {
    BigDecimal amount = null;
    Department department = null;
    boolean requiresManagerApproval = false;

    for (var arg : args) {
      if (arg.startsWith("--amount=")) {
        amount = new BigDecimal(arg.substring("--amount=".length()));
      } else if (arg.startsWith("--department=")) {
        department = Department.valueOf(arg.substring("--department=".length()).toUpperCase());
      } else if (arg.startsWith("--managerApproval=")) {
        requiresManagerApproval =
            Boolean.parseBoolean(arg.substring("--managerApproval=".length()));
      }
    }

    if (amount == null) {
      throw new IllegalArgumentException("Missing required argument: --amount");
    }
    if (department == null) {
      throw new IllegalArgumentException("Missing required argument: --department");
    }

    return new InvoiceRequest(amount, department, requiresManagerApproval);
  }

  private void printUsage() {
    System.out.println(
        "\nUsage: java -jar approval-workflow.jar --amount=<amount> --department=<department> --managerApproval=<true|false>");
    System.out.println("\nArguments:");
    System.out.println("  --amount=<value>           Invoice amount (required)");
    System.out.println(
        "  --department=<value>       Department: MARKETING, OTHER (required)");
    System.out.println(
        "  --managerApproval=<value>  Whether manager approval is required: true or false (optional, default: false)");
    System.out.println("\nExample:");
    System.out.println(
        "  java -jar approval-workflow.jar --amount=15000 --department=MARKETING --managerApproval=false");
  }
}
