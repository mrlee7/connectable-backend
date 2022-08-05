package com.backend.connectable.user.service;

import com.backend.connectable.event.domain.Ticket;
import com.backend.connectable.event.service.EventService;
import com.backend.connectable.klip.service.KlipService;
import com.backend.connectable.klip.service.dto.KlipAuthLoginResponse;
import com.backend.connectable.security.ConnectableUserDetails;
import com.backend.connectable.security.JwtProvider;
import com.backend.connectable.user.domain.User;
import com.backend.connectable.user.domain.repository.UserRepository;
import com.backend.connectable.user.ui.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KlipService klipService;
    private final JwtProvider jwtProvider;
    private final EventService eventService;

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        String requestKey = userLoginRequest.getRequestKey();
        KlipAuthLoginResponse klipAuthLoginResponse = klipService.authLogin(requestKey);
        if (klipAuthLoginResponse.isPrepared()) {
            return UserLoginResponse.ofPrepared();
        }
        if (klipAuthLoginResponse.isFailed()) {
            return UserLoginResponse.ofFailed();
        }
        return completeLogin(klipAuthLoginResponse.getKlaytnAddress());
    }

    private UserLoginSuccessResponse completeLogin(String klaytnAddress) {
        Optional<User> optionalUser = userRepository.findByKlaytnAddressAndIsActive(klaytnAddress, true);
        return optionalUser.map(user -> completeRegisteredUserLogin(klaytnAddress, user))
                .orElseGet(() -> completeNewUserLogin(klaytnAddress));
    }

    private UserLoginSuccessResponse completeRegisteredUserLogin(String klaytnAddress, User user) {
        if (user.hasNickname() && user.hasPhoneNumber()) {
            return UserLoginSuccessResponse.of(user, jwtProvider.generateToken(klaytnAddress), false);
        }
        return UserLoginSuccessResponse.of(user, jwtProvider.generateToken(klaytnAddress), true);
    }

    private UserLoginSuccessResponse completeNewUserLogin(String klaytnAddress) {
        User user = User.builder()
                .klaytnAddress(klaytnAddress)
                .isActive(true)
                .build();
        userRepository.save(user);
        return UserLoginSuccessResponse.of(
                user,
                jwtProvider.generateToken(klaytnAddress),
                true
        );
    }

    public UserResponse getUserByUserDetails(ConnectableUserDetails userDetails) {
        User user = userDetails.getUser();
        if (user.hasNickname() && user.hasPhoneNumber()) {
            return UserResponse.ofSuccess(user);
        }
        return UserResponse.ofFailure(user);
    }

    public UserModifyResponse deleteUserByUserDetails(ConnectableUserDetails userDetails) {
        User user = userDetails.getUser();
        userRepository.deleteUser(user.getKlaytnAddress());
        return UserModifyResponse.ofSuccess();
    }

    public UserModifyResponse modifyUserByUserDetails(ConnectableUserDetails userDetails, UserModifyRequest userModifyRequest) {
        User user = userDetails.getUser();
        log.info("@@USER_DETAILS_USER_OBJECT::{}", user);
        userRepository.modifyUser(user.getKlaytnAddress(), userModifyRequest.getNickname(), userModifyRequest.getPhoneNumber());
        return UserModifyResponse.ofSuccess();
    }

    public UserTicketListResponse getUserTicketsByUserDetails(ConnectableUserDetails userDetails) {
        String userKlaytnAddress = userDetails.getUser().getKlaytnAddress();
        List<Ticket> tickets = eventService.findTicketByUserAddress(userKlaytnAddress);
        List<UserTicketResponse> userTicketResponses = UserTicketResponse.toList(tickets);
        return UserTicketListResponse.of(userTicketResponses);
    }

    public UserValidationResponse validateNickname(String nickname) {
        boolean isExistingNickname = userRepository.existsByNickname(nickname);
        if (isExistingNickname) {
            return UserValidationResponse.ofUnavailable();
        }
        return UserValidationResponse.ofAvailable();
    }
}
