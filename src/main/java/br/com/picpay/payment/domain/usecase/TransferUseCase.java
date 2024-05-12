package br.com.picpay.payment.domain.usecase;

import org.springframework.stereotype.Service;

import br.com.picpay.payment.domain.entity.Transaction;
import br.com.picpay.payment.domain.entity.User;
import br.com.picpay.payment.domain.handler.NotEnoughBalanceException;
import br.com.picpay.payment.domain.handler.UserNotFoundException;
import br.com.picpay.payment.infrastructure.database.repository.TransactionRepository;
import br.com.picpay.payment.infrastructure.database.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TransferUseCase {
  private UserRepository userRepository;
  private TransactionRepository transactionRepository;

  public void transfer(Long payerId, Long payeeId, double value) {

    if (payerId == null || payeeId == null) {
      throw new IllegalArgumentException("payerId e payeeId não podem ser nulos");
    }
    userRepository.findAll().forEach(user -> System.out.println(user));

    User payer = userRepository.findById(payerId).orElseThrow(() -> new UserNotFoundException());
    User payee = userRepository.findById(payeeId).orElseThrow(() -> new UserNotFoundException());

    if (payer.getBalance() < value) {
      throw new NotEnoughBalanceException();
    }

    payer.setBalance(payer.getBalance() - value);
    payee.setBalance(payee.getBalance() + value);

    userRepository.save(payer);
    userRepository.save(payee);

    Transaction transaction = new Transaction(payer.getUserId(), payee.getUserId(), value);
    transactionRepository.save(transaction);
    log.info("Transaction saved: {}", transaction);
  }
}
